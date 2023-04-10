package net.thenextlvl.hologram;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CraftHologramLoader implements HologramLoader {
    private final Map<Player, Set<Hologram>> holograms = new LinkedHashMap<>();

    @Override
    public void load(Hologram hologram, Player player) {
        Preconditions.checkArgument(!isLoaded(hologram, player), "Hologram is already loaded");
        var holograms = this.holograms.getOrDefault(player, new HashSet<>());
        holograms.add(hologram);
        this.holograms.put(player, holograms);
        send(hologram, player);
    }

    private void send(Hologram hologram, Player player) {
    }

    @Override
    public void unload(Hologram hologram, Player player) {
        Preconditions.checkArgument(isLoaded(hologram, player), "Hologram is not loaded");
        var holograms = this.holograms.get(player);
        holograms.remove(hologram);
        this.holograms.put(player, holograms);
        remove(hologram, player);
    }

    private void remove(Hologram hologram, Player player) {
    }

    @Override
    public boolean isLoaded(Hologram hologram, Player player) {
        var set = holograms.get(player);
        return set != null && set.contains(hologram);
    }
}
