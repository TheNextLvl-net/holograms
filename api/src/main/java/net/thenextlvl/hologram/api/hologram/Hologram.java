package net.thenextlvl.hologram.api.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Display;

/**
 * An interface that represents a hologram
 */
public interface Hologram extends Display {
    /**
     * Get the location of the hologram
     *
     * @return the current location the hologram is
     */
    Location getLocation();

    /**
     * Set the location of the hologram<br>
     * <i>Only applies on load, not for teleportation or updating</i>
     *
     * @param location the location the hologram should appear
     */
    void setLocation(Location location);
}
