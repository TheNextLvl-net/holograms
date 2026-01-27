package net.thenextlvl.hologram.event;

import net.thenextlvl.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Called when a hologram is teleported.
 *
 * @since 0.5.0
 */
public class HologramTeleportEvent extends HologramEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Location from;
    private Location to;
    private boolean cancelled;

    @ApiStatus.Internal
    public HologramTeleportEvent(final Hologram hologram, final Location from, final Location to) {
        super(hologram);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the location the hologram is teleporting from.
     *
     * @return the origin location
     * @since 0.5.0
     */
    @Contract(value = " -> new", pure = true)
    public Location getFrom() {
        return from.clone();
    }

    /**
     * Returns the location the hologram is teleporting to.
     *
     * @return the destination location
     * @since 0.5.0
     */
    @Contract(value = " -> new", pure = true)
    public Location getTo() {
        return to.clone();
    }

    /**
     * Sets the location the hologram will teleport to.
     *
     * @param to the destination location
     * @since 0.5.0
     */
    @Contract(mutates = "this")
    public void setTo(final Location to) {
        this.to = to.clone();
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
