package net.thenextlvl.hologram.listeners;

import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class EntityListener implements Listener {
    private final HologramPlugin plugin;

    public EntityListener(HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityRemove(EntityRemoveEvent event) {
        plugin.hologramProvider().getHologram(event.getEntity()).ifPresent(hologram -> {
            hologram.persist();
            ((PaperHologram) hologram).invalidate();
        });
    }
}
