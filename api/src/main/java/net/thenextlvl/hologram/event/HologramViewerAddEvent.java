package net.thenextlvl.hologram.event;

import net.thenextlvl.hologram.Hologram;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.UUID;

/**
 * Called when a viewer is added to a hologram.
 *
 * @since 0.6.0
 */
public final class HologramViewerAddEvent extends HologramEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final UUID viewer;
    private boolean cancelled;

    @ApiStatus.Internal
    public HologramViewerAddEvent(final Hologram hologram, final UUID viewer) {
        super(hologram);
        this.viewer = viewer;
    }

    /**
     * Returns the viewer being added.
     *
     * @return the viewer UUID
     * @since 0.6.0
     */
    @Contract(pure = true)
    public UUID getViewer() {
        return viewer;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Contract(pure = true)
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
