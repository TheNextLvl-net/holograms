package net.thenextlvl.hologram.event;

import net.thenextlvl.hologram.Hologram;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Called when a hologram is unloaded.
 *
 * @since 0.5.0
 */
public class HologramUnloadEvent extends HologramEvent {
    private static final HandlerList handlers = new HandlerList();

    @ApiStatus.Internal
    public HologramUnloadEvent(final Hologram hologram) {
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
