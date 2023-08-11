package net.thenextlvl.hologram.v1_20_R1;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.thenextlvl.hologram.api.Hologram;
import net.thenextlvl.hologram.api.HologramLoader;
import net.thenextlvl.hologram.v1_20_R1.line.CraftHologramLine;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftDisplay;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class CraftHologramLoader implements HologramLoader {
    private final ClientsideHologramLoader loader = new ClientsideHologramLoader(new HologramCache());

    @Override
    public void load(Hologram hologram, Player player) throws IllegalArgumentException, NullPointerException {
        Preconditions.checkArgument(!isLoaded(hologram, player), "Hologram is already loaded");
        Preconditions.checkArgument(canSee(player, hologram), "Hologram can't be seen by the player");
        Preconditions.checkNotNull(hologram.getLocation().getWorld(), "World can't be null");
        loader.load((CraftHologram) hologram, (CraftPlayer) player);
    }

    @Override
    public void unload(Hologram hologram) {
        getViewers(hologram).forEach(player -> unload(hologram, player));
    }

    @Override
    public void unload(Hologram hologram, Player player) throws IllegalArgumentException {
        Preconditions.checkArgument(isLoaded(hologram, player), "Hologram is not loaded");
        loader.unload((CraftHologram) hologram, (CraftPlayer) player);
    }

    @Override
    public void update(Hologram hologram) throws NullPointerException {
        getViewers(hologram).forEach(player -> update(hologram, player));
    }

    @Override
    public void update(Hologram hologram, Player player) throws IllegalArgumentException, NullPointerException {
        loader.update((CraftHologram) hologram, (CraftPlayer) player);
    }

    @Override
    public boolean isLoaded(Hologram hologram, Player player) {
        var holograms = loader.cache().get(player);
        return holograms != null && holograms.containsKey((CraftHologram) hologram);
    }

    @Override
    public Collection<Player> getViewers(Hologram hologram) {
        return loader.cache().keySet().stream()
                .filter(player -> isLoaded(hologram, player))
                .toList();
    }

    @Override
    public boolean canSee(Player player, Hologram hologram) {
        return player.getWorld().equals(hologram.getLocation().getWorld());
    }

    @Override
    public Collection<CraftHologram> getHolograms(Player player) {
        return loader.cache().getHolograms(player).keySet();
    }

    private record ClientsideHologramLoader(HologramCache cache) {

        private void load(CraftHologram hologram, CraftPlayer player) {
            cache.addHologram(player, hologram);
            var location = hologram.getLocation().clone();
            var connection = player.getHandle().connection;
            var displays = new ArrayList<CraftDisplay>();
            hologram.getLines().forEach(line -> {
                var display = line.display(location);
                cache.addHologramLine(player, hologram, line, display);
                displays.add(display);
            });
            displays.stream().map(display -> {
                var list = display.getHandle().getEntityData().packDirty();
                var values = list != null ? list : new ArrayList<SynchedEntityData.DataValue<?>>();
                var entityDataPacket = new ClientboundSetEntityDataPacket(display.getEntityId(), values);
                var entityAddPacket = display.getHandle().getAddEntityPacket();
                return Arrays.asList(entityAddPacket, entityDataPacket);
            }).forEach(packets -> packets.forEach(connection::send));
        }

        private void unload(CraftHologram hologram, CraftPlayer player) {
            var connection = player.getHandle().connection;
            var ids = new IntArrayList(cache.getHologramIds(player, hologram));
            connection.send(new ClientboundRemoveEntitiesPacket(ids));
            cache.removeHologram(player, hologram);
        }

        private void update(CraftHologram hologram, CraftPlayer player) {
            var connection = player.getHandle().connection;
            var location = hologram.getLocation().clone();
            cache().getHologramLines(player, hologram).forEach((line, display) -> {
                var list = display.getHandle().getEntityData().packDirty();
                var values = list != null ? list : new ArrayList<SynchedEntityData.DataValue<?>>();
                connection.send(new ClientboundSetEntityDataPacket(display.getEntityId(), values));
                connection.send(new ClientboundTeleportEntityPacket(display.getHandle()));
            });
        }
    }

    private static class HologramCache extends WeakHashMap<Player, Map<CraftHologram, Map<CraftHologramLine, CraftDisplay>>> {

        private Map<CraftHologram, Map<CraftHologramLine, CraftDisplay>> getHolograms(Player player) {
            return getOrDefault(player, new HashMap<>());
        }

        private void addHologram(Player player, CraftHologram hologram) {
            var holograms = getHolograms(player);
            holograms.put(hologram, new HashMap<>());
            put(player, holograms);
        }

        private void removeHologram(Player player, CraftHologram hologram) {
            getHolograms(player).remove(hologram);
        }

        private Map<CraftHologramLine, CraftDisplay> getHologramLines(Player player, CraftHologram hologram) {
            return getHolograms(player).getOrDefault(hologram, new HashMap<>());
        }

        private List<Integer> getHologramIds(Player player, CraftHologram hologram) {
            return getHologramLines(player, hologram).values().stream().map(CraftEntity::getEntityId).toList();
        }

        private void setHologramLines(Player player, CraftHologram hologram, Map<CraftHologramLine, CraftDisplay> lines) {
            var holograms = getHolograms(player);
            holograms.put(hologram, lines);
            put(player, holograms);
        }

        private void addHologramLine(Player player, CraftHologram hologram, CraftHologramLine line, CraftDisplay display) {
            var lines = getHologramLines(player, hologram);
            lines.put(line, display);
            setHologramLines(player, hologram, lines);
        }
    }
}
