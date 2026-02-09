package net.thenextlvl.hologram.event;

import net.thenextlvl.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Called when an event related to a hologram occurs.
 *
 * @since 0.3.1
 */
abstract class HologramEvent extends Event {
    private final Hologram hologram;

    @ApiStatus.Internal
    protected HologramEvent(final Hologram hologram) {
        super(!Bukkit.isPrimaryThread());
        this.hologram = hologram;
    }

    /**
     * Returns the hologram.
     *
     * @return the hologram
     * @since 0.3.1
     */
    @Contract(pure = true)
    public Hologram getHologram() {
        return hologram;
    }
}
