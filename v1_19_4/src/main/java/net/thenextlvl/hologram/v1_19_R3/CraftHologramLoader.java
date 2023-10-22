package net.thenextlvl.hologram.v1_19_R3;

import com.google.common.base.Preconditions;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.thenextlvl.hologram.api.HologramLoader;
import net.thenextlvl.hologram.api.hologram.Hologram;
import net.thenextlvl.hologram.v1_19_R3.hologram.CraftHologram;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class CraftHologramLoader implements HologramLoader {
    private final ClientHologramLoader loader = new ClientHologramLoader();

    @Override
    public void load(Hologram hologram, Player player) throws IllegalArgumentException, NullPointerException {
        Preconditions.checkArgument(!isLoaded(hologram, player), "Hologram is already loaded");
        Preconditions.checkArgument(canSee(player, hologram), "Hologram can't be seen by the player");
        Preconditions.checkNotNull(hologram.getLocation().getWorld(), "World can't be null");
        loader.load((CraftHologram) hologram, (CraftPlayer) player);
    }

    @Override
    public void unload(Hologram hologram, Player player) throws IllegalArgumentException {
        Preconditions.checkArgument(isLoaded(hologram, player), "Hologram is not loaded");
        loader.unload((CraftHologram) hologram, (CraftPlayer) player);
    }

    @Override
    public void update(Hologram hologram, Player player) throws IllegalArgumentException, NullPointerException {
        loader.update((CraftHologram) hologram, (CraftPlayer) player);
    }

    @Override
    public void teleport(Hologram hologram, Location location, Player player) throws IllegalArgumentException, NullPointerException {
        Preconditions.checkArgument(isLoaded(hologram, player), "Hologram is not loaded");
        Preconditions.checkArgument(canSee(player, hologram), "Hologram can't be seen by the player");
        Preconditions.checkNotNull(hologram.getLocation().getWorld(), "World can't be null");
        Preconditions.checkArgument(hologram.getLocation().getWorld().equals(location.getWorld()),
                "Hologram world can't change");
        loader.teleport((CraftHologram) hologram, location, (CraftPlayer) player);
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

        private void load(CraftHologram hologram, CraftPlayer player) {
            addHologram(player, hologram);
            player.getHandle().connection.send(hologram.updateEntityDisplay().getHandle().getAddEntityPacket());
            update(hologram, player);
        }

        private void unload(CraftHologram hologram, CraftPlayer player) {
            if (hologram.getEntityDisplay() == null) return;
            int entityId = hologram.getEntityDisplay().getEntityId();
            player.getHandle().connection.send(new ClientboundRemoveEntitiesPacket(entityId));
            removeHologram(player, hologram);
        }

        private void update(CraftHologram hologram, CraftPlayer player) {
            var display = hologram.updateEntityDisplay();
            var list = display.getHandle().getEntityData().packDirty();
            var values = list != null ? list : new ArrayList<SynchedEntityData.DataValue<?>>();
            player.getHandle().connection.send(new ClientboundSetEntityDataPacket(display.getEntityId(), values));
        }

        private void teleport(CraftHologram hologram, Location location, CraftPlayer player) {
            var display = hologram.getEntityDisplay();
            if (display == null) return;
            var connection = player.getHandle().connection;
            display.getHandle().moveTo(location.getX(), location.getY(), location.getZ());
            connection.send(new ClientboundTeleportEntityPacket(display.getHandle()));
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
