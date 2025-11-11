package net.thenextlvl.hologram.listener;

import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ChunkListener implements Listener {
    private final HologramPlugin plugin;

    public ChunkListener(HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        plugin.hologramController().getHolograms(event.getChunk())
                .filter(hologram -> !hologram.isSpawned())
                .forEach(Hologram::spawn);
    }
}
