package net.thenextlvl.hologram.implementation;

import com.google.common.base.Preconditions;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.thenextlvl.hologram.api.HologramLoader;
import net.thenextlvl.hologram.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftDisplay;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;

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
        return loader.getHolograms(player);
    }

    private static class ClientHologramLoader extends WeakHashMap<Player, Set<Hologram>> {

        private void load(Hologram hologram, CraftPlayer player) {
            addHologram(player, hologram);
            var display = ((CraftDisplay) hologram).getHandle();
            player.getHandle().connection.send(display.getAddEntityPacket());
            update(hologram, player);
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
            connection.send(new ClientboundTeleportEntityPacket(display));
        }

        private Set<Hologram> getHolograms(Player player) {
            return getOrDefault(player, new HashSet<>());
        }

        private void addHologram(Player player, Hologram hologram) {
            var holograms = getHolograms(player);
            holograms.add(hologram);
            put(player, holograms);
        }

        private void removeHologram(Player player, Hologram hologram) {
            getHolograms(player).remove(hologram);
        }
    }
}