package net.thenextlvl.hologram.event;

import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.HologramLine;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Called when a line is added to a hologram.
 *
 * @since 0.8.0
 */
public final class HologramLineAddEvent extends HologramEvent {
    private static final HandlerList handlers = new HandlerList();
    private final HologramLine line;

    @ApiStatus.Internal
    public HologramLineAddEvent(final Hologram hologram, final HologramLine line) {
        super(hologram);
        this.line = line;
    }

    /**
     * Returns the line that was added.
     *
     * @return the added line
     * @since 0.8.0
     */
    @Contract(pure = true)
    public HologramLine getLine() {
        return line;
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
