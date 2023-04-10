package net.thenextlvl.hologram;

import net.thenextlvl.hologram.line.HologramLine;
import org.bukkit.Location;

import java.util.Collection;

public interface Hologram {

    Location getLocation();

    void setLocation(Location location);

    Collection<HologramLine> getLines();

    void setLines(Collection<HologramLine> lines);
}
