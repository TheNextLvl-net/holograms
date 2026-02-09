package net.thenextlvl.hologram.event;

import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.StaticHologramLine;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Called when a page is removed from a {@link PagedHologramLine}.
 *
 * @since 0.8.0
 */
public final class HologramPageRemoveEvent extends HologramEvent {
    private static final HandlerList handlers = new HandlerList();
    private final PagedHologramLine line;
    private final StaticHologramLine page;

    @ApiStatus.Internal
    public HologramPageRemoveEvent(final Hologram hologram, final PagedHologramLine line, final StaticHologramLine page) {
        super(hologram);
        this.line = line;
        this.page = page;
    }

    /**
     * Returns the paged line the page was removed from.
     *
     * @return the paged hologram line
     * @since 0.8.0
     */
    @Contract(pure = true)
    public PagedHologramLine getLine() {
        return line;
    }

    /**
     * Returns the page that was removed.
     *
     * @return the removed page
     * @since 0.8.0
     */
    @Contract(pure = true)
    public StaticHologramLine getPage() {
        return page;
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
