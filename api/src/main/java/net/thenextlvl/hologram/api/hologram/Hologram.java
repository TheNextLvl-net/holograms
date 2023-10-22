package net.thenextlvl.hologram.api.hologram;

import net.thenextlvl.hologram.api.display.EntityDisplay;
import org.bukkit.Location;

/**
 * An interface that represents a hologram
 */
public interface Hologram extends EntityDisplay {
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
