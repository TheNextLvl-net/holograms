package net.thenextlvl.hologram.listeners;

import net.thenextlvl.hologram.HologramPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLocaleChangeEvent;

public final class LocaleListener implements Listener {
    private final HologramPlugin plugin;

    public LocaleListener(HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLocaleChange(PlayerLocaleChangeEvent event) {
        plugin.updateHologramTextLines(event.getPlayer());
    }
}
