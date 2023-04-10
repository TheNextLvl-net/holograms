package net.thenextlvl.hologram;

import org.bukkit.entity.Player;

public interface HologramLoader {

    void load(Hologram hologram, Player player);

    void unload(Hologram hologram, Player player);

    boolean isLoaded(Hologram hologram, Player player);
}
