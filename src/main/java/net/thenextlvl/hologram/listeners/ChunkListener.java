package net.thenextlvl.hologram.listeners;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import io.papermc.paper.event.player.PlayerUntrackEntityEvent;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.models.line.PaperHologramLine;
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

    // todo: test entity untracking
    // @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkUnload(final PlayerUntrackEntityEvent event) {
        plugin.hologramProvider().getHologramLine(event.getEntity())
                .map(hologram -> (PaperHologramLine) hologram)
                .ifPresent(hologram -> hologram.invalidate(event.getEntity()));
    }
}
