package net.thenextlvl.hologram.api;

import org.jspecify.annotations.NullMarked;

/**
 * A class that provides all necessities for managing holograms
 */
@NullMarked
public interface HologramProvider {
    /**
     * Get the corresponding {@link HologramRegistry}
     *
     * @return the {@link HologramRegistry}
     */
    HologramRegistry getHologramRegistry();

    /**
     * Get the corresponding {@link HologramFactory}
     *
     * @return the {@link HologramFactory}
     */
    HologramFactory getHologramFactory();
}
