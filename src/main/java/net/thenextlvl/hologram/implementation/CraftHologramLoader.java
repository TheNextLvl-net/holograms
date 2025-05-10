package net.thenextlvl.hologram.implementation;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import net.thenextlvl.hologram.api.HologramLoader;
import net.thenextlvl.hologram.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftDisplay;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

@NullMarked
public class CraftHologramLoader implements HologramLoader {
    private final ClientHologramLoader loader = new ClientHologramLoader();

    @Override
    public void load(Hologram hologram, Player player) throws IllegalArgumentException, NullPointerException {
        Preconditions.checkArgument(!isLoaded(hologram, player), "Hologram is already loaded");
        Preconditions.checkArgument(canSee(player, hologram), "Hologram can't be seen by the player");
        Preconditions.checkNotNull(hologram.getLocation().getWorld(), "World can't be null");
        loader.load(hologram, (CraftPlayer) player);
    }

    @Override
    public void unload(Hologram hologram, Player player) throws IllegalArgumentException {
        Preconditions.checkArgument(isLoaded(hologram, player), "Hologram is not loaded");
        loader.unload(hologram, (CraftPlayer) player);
    }

    @Override
    public void update(Hologram hologram, Player player) throws IllegalArgumentException, NullPointerException {
        Preconditions.checkArgument(isLoaded(hologram, player), "Hologram is not loaded");
        Preconditions.checkNotNull(hologram.getLocation().getWorld(), "World can't be null");
        loader.update(hologram, (CraftPlayer) player);
    }

    @Override
    public void teleport(Hologram hologram, Location location, Player player) throws IllegalArgumentException, NullPointerException {
        Preconditions.checkArgument(isLoaded(hologram, player), "Hologram is not loaded");
        Preconditions.checkNotNull(hologram.getLocation().getWorld(), "World can't be null");
        Preconditions.checkArgument(hologram.getLocation().getWorld().equals(location.getWorld()),
                "Hologram world can't change");
        loader.teleport(hologram, location, (CraftPlayer) player);
    }

    @Override
    public Collection<Player> getViewers(Hologram hologram) {
        return loader.keySet().stream()
                .filter(player -> isLoaded(hologram, player))
                .toList();
    }

    @Override
    public Collection<Hologram> getHolograms(Player player) {
        return ImmutableList.copyOf(loader.getHolograms(player));
    }

    private static class ClientHologramLoader extends WeakHashMap<Player, Set<Hologram>> {

        private void load(Hologram hologram, CraftPlayer player) {
            addHologram(player, hologram);
            CraftDisplay display = (CraftDisplay) hologram;
            player.getHandle().connection.send(createAddEntityPacket(display));
            update(hologram, player);
        }

        private Packet<?> createAddEntityPacket(CraftDisplay display) {
            return new ClientboundAddEntityPacket(
                    display.getEntityId(),
                    display.getUniqueId(),
                    display.getX(),
                    display.getY(),
                    display.getZ(),
                    display.getPitch(),
                    display.getYaw(),
                    display.getHandle().getType(),
                    0,
                    new Vec3(0, 0, 0),
                    display.getYaw()
            );
        }

        private void unload(Hologram hologram, CraftPlayer player) {
            player.getHandle().connection.send(new ClientboundRemoveEntitiesPacket(hologram.getEntityId()));
            removeHologram(player, hologram);
        }

        private void update(Hologram hologram, CraftPlayer player) {
            var list = ((CraftDisplay) hologram).getHandle().getEntityData().packAll();
            var values = list != null ? list : new ArrayList<SynchedEntityData.DataValue<?>>();
            player.getHandle().connection.send(new ClientboundSetEntityDataPacket(hologram.getEntityId(), values));
        }

        private void teleport(Hologram hologram, Location location, CraftPlayer player) {
            var connection = player.getHandle().connection;
            var display = ((CraftDisplay) hologram).getHandle();
            display.moveTo(location.getX(), location.getY(), location.getZ());
            connection.send(new ClientboundTeleportEntityPacket(
                    display.getId(),
                    PositionMoveRotation.of(display),
                    Set.of(),
                    display.onGround()
            ));
        }

        private Set<Hologram> getHolograms(Player player) {
            return computeIfAbsent(player, ignored -> new HashSet<>());
        }

        private void addHologram(Player player, Hologram hologram) {
            getHolograms(player).add(hologram);
        }

        private void removeHologram(Player player, Hologram hologram) {
            getHolograms(player).remove(hologram);
        }
    }
}