package net.thenextlvl.hologram.listeners;

import net.thenextlvl.hologram.HologramPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ConnectionListener implements Listener {
    private final HologramPlugin plugin;

    public ConnectionListener(final HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        plugin.hologramProvider().getHolograms(event.getPlayer())
                .forEach(hologram -> hologram.despawn(event.getPlayer()));
    }
}
