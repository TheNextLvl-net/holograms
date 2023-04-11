package net.thenextlvl.hologram.v1_19_R3;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.thenextlvl.hologram.api.Hologram;
import net.thenextlvl.hologram.api.HologramLoader;
import net.thenextlvl.hologram.v1_19_R3.line.CraftHologramLine;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftDisplay;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class CraftHologramLoader implements HologramLoader {
    private final ClientsideHologramLoader loader = new ClientsideHologramLoader(new HologramCache());

    @Override
    public void load(Hologram hologram, Player player) {
        Preconditions.checkArgument(!isLoaded(hologram, player), "Hologram is already loaded");
        Preconditions.checkArgument(canSee(player, hologram), "Hologram can't be seen by the player");
        Preconditions.checkNotNull(hologram.getLocation().getWorld(), "World can't be null");
        loader.load((CraftHologram) hologram, (CraftPlayer) player);
    }

    @Override
    public void unload(Hologram hologram, Player player) {
        Preconditions.checkArgument(isLoaded(hologram, player), "Hologram is not loaded");
        loader.unload((CraftHologram) hologram, (CraftPlayer) player);
    }

    @Override
    public void update(Hologram hologram, Player player) {
        loader.update((CraftHologram) hologram, (CraftPlayer) player);
    }

    @Override
    public boolean isLoaded(Hologram hologram, Player player) {
        var holograms = loader.cache().get(player);
        return holograms != null && holograms.containsKey((CraftHologram) hologram);
    }

    @Override
    public boolean canSee(Player player, Hologram hologram) {
        return player.getWorld().equals(hologram.getLocation().getWorld());
    }

    @Override
    public Collection<CraftHologram> getHolograms(Player player) {
        return loader.cache().getHolograms(player).keySet();
    }

    @Override
    public Collection<CraftHologram> getHolograms(Player player, World world) {
        return getHolograms(player).stream().filter(hologram -> hologram.getLocation().getWorld().equals(world)).toList();
    }

    private record ClientsideHologramLoader(HologramCache cache) {

        private void load(CraftHologram hologram, CraftPlayer player) {
            cache.addHologram(player, hologram);
            var location = hologram.getLocation().clone();
            var connection = player.getHandle().connection;
            var displays = new ArrayList<CraftDisplay>();
            hologram.getLines().forEach(line -> {
                var display = line.display(location, craftDisplay ->
                        location.setY(location.getY() - craftDisplay.getDisplayHeight()));
                cache.addHologramLine(player, hologram, line, display.getEntityId());
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
            var ids = new IntArrayList(cache.getHologramLines(player, hologram).values());
            connection.send(new ClientboundRemoveEntitiesPacket(ids));
            cache.removeHologram(player, hologram);
        }

        private void update(CraftHologram hologram, CraftPlayer player) {
            unload(hologram, player);
            load(hologram, player);
        }
    }

    private static class HologramCache extends WeakHashMap<Player, Map<CraftHologram, Map<CraftHologramLine, Integer>>> {

        private Map<CraftHologram, Map<CraftHologramLine, Integer>> getHolograms(Player player) {
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

        private Map<CraftHologramLine, Integer> getHologramLines(Player player, CraftHologram hologram) {
            return getHolograms(player).getOrDefault(hologram, new HashMap<>());
        }

        private void setHologramLines(Player player, CraftHologram hologram, Map<CraftHologramLine, Integer> lines) {
            var holograms = getHolograms(player);
            holograms.put(hologram, lines);
            put(player, holograms);
        }

        private void addHologramLine(Player player, CraftHologram hologram, CraftHologramLine line, int id) {
            var lines = getHologramLines(player, hologram);
            lines.put(line, id);
            setHologramLines(player, hologram, lines);
        }
    }
}
