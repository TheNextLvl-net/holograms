package net.thenextlvl.hologram.listeners;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import net.thenextlvl.hologram.HologramPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ChunkListener implements Listener {
    private final HologramPlugin plugin;

    public ChunkListener(final HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(final PlayerChunkLoadEvent event) {
        plugin.hologramProvider().getHolograms(event.getChunk())
                .forEach(hologram -> hologram.spawn(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkUnload(final PlayerChunkUnloadEvent event) {
        plugin.hologramProvider().getHolograms(event.getChunk())
                .forEach(hologram -> hologram.despawn(event.getPlayer()));
    }
}
