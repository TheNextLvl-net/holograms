package net.thenextlvl.hologram.line;

import org.bukkit.Location;
import org.bukkit.entity.Display;

public interface HologramLine {
    Display display(Location location);

    LineType getType();
}
