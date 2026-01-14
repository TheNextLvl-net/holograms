package net.thenextlvl.hologram.event;

import net.thenextlvl.hologram.Hologram;
import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

abstract class HologramEvent extends Event {
    private final Hologram hologram;

    @ApiStatus.Internal
    protected HologramEvent(final Hologram hologram) {
        this.hologram = hologram;
    }

    /**
     * Returns the hologram.
     *
     * @return the hologram
     * @since 0.5.0
     */
    @Contract(pure = true)
    public Hologram getHologram() {
        return hologram;
    }
}
