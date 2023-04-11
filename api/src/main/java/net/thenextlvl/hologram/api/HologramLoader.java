package net.thenextlvl.hologram.api;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface HologramLoader {

    void load(Hologram hologram, Player player);

    void unload(Hologram hologram, Player player);

    void update(Hologram hologram, Player player);

    boolean isLoaded(Hologram hologram, Player player);

    boolean canSee(Player player, Hologram hologram);

    Collection<? extends Hologram> getHolograms(Player player);

    Collection<? extends Hologram> getHolograms(Player player, World world);
}
