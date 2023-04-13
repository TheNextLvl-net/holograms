package net.thenextlvl.hologram.api;

/**
 * A class that provides all necessities for managing holograms
 */
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

    /**
     * Get the corresponding {@link HologramLoader}
     *
     * @return the {@link HologramLoader}
     */
    HologramLoader getHologramLoader();
}
