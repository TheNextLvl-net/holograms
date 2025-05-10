package net.thenextlvl.hologram.listener;

import net.thenextlvl.hologram.HologramPlugin;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class HologramListener implements Listener {
    private final HologramPlugin plugin;

    public HologramListener(HologramPlugin plugin) {
        this.plugin = plugin;
    }
}
