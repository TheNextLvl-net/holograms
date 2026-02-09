package net.thenextlvl.hologram.event;

import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.StaticHologramLine;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Called when the displayed page of a {@link PagedHologramLine} changes for a player.
 * <p>
 * This event is fired for both manual page changes ({@link PagedHologramLine#setPage},
 * {@link PagedHologramLine#cyclePage}) and automatic cycling.
 *
 * @since 0.8.0
 */
public final class HologramPageChangeEvent extends HologramEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final PagedHologramLine line;
    private final StaticHologramLine newPage;
    private final StaticHologramLine oldPage;
    private final int newIndex;
    private final int oldIndex;
    private final Player player;
    private boolean cancelled;

    @ApiStatus.Internal
    public HologramPageChangeEvent(
            final Hologram hologram, final PagedHologramLine line, final Player player,
            final StaticHologramLine oldPage, final StaticHologramLine newPage,
            final int oldIndex, final int newIndex
    ) {
        super(hologram);
        this.line = line;
        this.player = player;
        this.oldPage = oldPage;
        this.newPage = newPage;
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }

    /**
     * Returns the paged line whose page is changing.
     *
     * @return the paged hologram line
     * @since 0.8.0
     */
    @Contract(pure = true)
    public PagedHologramLine getLine() {
        return line;
    }

    /**
     * Returns the player for whom the page is changing.
     *
     * @return the player
     * @since 0.8.0
     */
    @Contract(pure = true)
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the page that was previously displayed.
     *
     * @return the old page
     * @since 0.8.0
     */
    @Contract(pure = true)
    public StaticHologramLine getOldPage() {
        return oldPage;
    }

    /**
     * Returns the page that will be displayed.
     *
     * @return the new page
     * @since 0.8.0
     */
    @Contract(pure = true)
    public StaticHologramLine getNewPage() {
        return newPage;
    }

    /**
     * Returns the index of the old page.
     *
     * @return the index of the old page
     * @since 0.8.0
     */
    @Contract(pure = true)
    public int getOldIndex() {
        return oldIndex;
    }

    /**
     * Returns the index of the new page.
     *
     * @return the index of the new page
     * @since 0.8.0
     */
    @Contract(pure = true)
    public int getNewIndex() {
        return newIndex;
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
