package net.thenextlvl.hologram.listeners;

import net.thenextlvl.hologram.HologramPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class LocaleListener implements Listener {
    private final HologramPlugin plugin;

    public LocaleListener(final HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLocaleChange(final PlayerLocaleChangeEvent event) {
        event.getPlayer().getScheduler().run(plugin, task -> {
            plugin.updateHologramTextLines(event.getPlayer());
        }, null);
    }
}
