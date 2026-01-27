package net.thenextlvl.hologram.event;

import net.thenextlvl.hologram.Hologram;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Called when a hologram is loaded from disk.
 *
 * @since 0.6.0
 */
public class HologramLoadEvent extends HologramEvent {
    private static final HandlerList handlers = new HandlerList();

    @ApiStatus.Internal
    public HologramLoadEvent(final Hologram hologram) {
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
