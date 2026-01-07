package net.thenextlvl.hologram.listeners;

import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class WorldListener implements Listener {
    private final HologramPlugin plugin;

    public WorldListener(HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        plugin.loadHolograms(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldSave(WorldSaveEvent event) {
        plugin.hologramProvider().getHolograms(event.getWorld()).forEach(Hologram::persist);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
        plugin.hologramProvider().holograms.removeIf(hologram -> {
            if (hologram.getWorld().equals(event.getWorld())) {
                hologram.persist();
                return true;
            } else return false;
        });
    }
}
