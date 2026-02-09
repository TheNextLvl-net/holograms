package net.thenextlvl.hologram.event;

import net.thenextlvl.hologram.Hologram;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Called when a hologram is deleted.
 *
 * @since 0.3.1
 */
public final class HologramDeleteEvent extends HologramEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    @ApiStatus.Internal
    public HologramDeleteEvent(final Hologram hologram) {
        super(hologram);
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
