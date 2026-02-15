package net.thenextlvl.hologram.event;

import net.thenextlvl.hologram.action.ClickType;
import net.thenextlvl.hologram.line.HologramLine;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Called when a player interacts with a hologram line.
 * <p>
 * Canceling this event will prevent all click actions from being invoked.
 *
 * @since 1.0.0
 */
public final class PlayerHologramInteractEvent extends HologramEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final HologramLine line;
    private final Player player;
    private final ClickType clickType;
    private boolean cancelled;

    @ApiStatus.Internal
    public PlayerHologramInteractEvent(final HologramLine line, final Player player, final ClickType clickType) {
        super(line.getHologram());
        this.line = line;
        this.player = player;
        this.clickType = clickType;
    }

    /**
     * Returns the hologram line that was interacted with.
     *
     * @return the hologram line
     * @since 1.0.0
     */
    @Contract(pure = true)
    public HologramLine getLine() {
        return line;
    }

    /**
     * Returns the player who interacted with the hologram.
     *
     * @return the player
     * @since 1.0.0
     */
    @Contract(pure = true)
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the type of click performed.
     *
     * @return the click type
     * @since 1.0.0
     */
    @Contract(pure = true)
    public ClickType getClickType() {
        return clickType;
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
