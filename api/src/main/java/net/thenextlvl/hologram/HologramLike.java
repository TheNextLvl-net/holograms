package net.thenextlvl.hologram;

import org.jetbrains.annotations.Contract;

import java.util.Optional;

/**
 * Represents a hologram-like object.
 * <p>
 * Mainly used for de/serialization.
 *
 * @since 0.12.0
 */
public interface HologramLike {
    /**
     * Returns the name of the hologram.
     *
     * @return hologram name
     * @since 0.12.0
     */
    @Contract(pure = true)
    String getName();

    /**
     * Returns the hologram.
     *
     * @return hologram
     * @since 0.12.0
     */
    @Contract(pure = true)
    Optional<Hologram> getHologram();
}
