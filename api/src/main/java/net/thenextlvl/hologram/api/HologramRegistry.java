package net.thenextlvl.hologram.api;

import net.thenextlvl.hologram.api.hologram.Hologram;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;

/**
 * A registry that holds all registered holograms
 */
@NullMarked
public interface HologramRegistry {
    /**
     * Get all the registered holograms
     *
     * @return the registered holograms
     */
    Collection<Hologram> getHolograms();

    /**
     * Register a new hologram
     *
     * @param hologram the hologram to register
     * @throws IllegalArgumentException thrown if the hologram is already registered
     */
    void register(Hologram hologram) throws IllegalArgumentException;

    /**
     * Unregister a registered hologram
     *
     * @param hologram the hologram to unregister
     * @throws IllegalArgumentException thrown if the hologram is not registered
     */
    void unregister(Hologram hologram) throws IllegalArgumentException;

    /**
     * Checks if a certain hologram is registered
     *
     * @param hologram the hologram
     * @return true if the hologram is registered
     */
    boolean isRegistered(Hologram hologram);
}
