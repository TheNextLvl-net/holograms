package net.thenextlvl.hologram.event;

import net.thenextlvl.hologram.Hologram;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Called when a hologram is created.
 *
 * @since 0.3.1
 */
public class HologramCreateEvent extends HologramEvent {
    private static final HandlerList handlers = new HandlerList();

    @ApiStatus.Internal
    public HologramCreateEvent(final Hologram hologram) {
        super(hologram);
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
