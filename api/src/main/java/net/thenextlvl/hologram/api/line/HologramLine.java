package net.thenextlvl.hologram.api.line;

import org.bukkit.Location;
import org.bukkit.entity.Display;

public interface HologramLine {
    /**
     * Get the type of the hologram line
     * @return the {@link LineType} enum
     */
    LineType getType();
}
