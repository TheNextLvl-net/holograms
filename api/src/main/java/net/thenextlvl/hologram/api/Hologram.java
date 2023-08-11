package net.thenextlvl.hologram.api;

import net.thenextlvl.hologram.api.line.HologramLine;
import org.bukkit.Location;

import java.util.Collection;

/**
 * An interface that represents a hologram
 */
public interface Hologram extends Cloneable {
    /**
     * Get the location of the hologram
     *
     * @return the current location the hologram is
     */
    Location getLocation();

    /**
     * Set the location of the hologram
     *
     * @param location the location the hologram should appear
     */
    void setLocation(Location location);

    /**
     * Get the lines of the hologram
     *
     * @return the lines of the hologram
     */
    Collection<? extends HologramLine> getLines();

    /**
     * Set the lines of the hologram
     *
     * @param lines the new lines
     */
    void setLines(Collection<? extends HologramLine> lines);

    /**
     * Creates a copy of this hologram object
     *
     * @return the clone of this hologram object
     */
    Hologram clone();
}
