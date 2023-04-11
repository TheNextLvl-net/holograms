package net.thenextlvl.hologram.api;

import net.thenextlvl.hologram.api.line.HologramLine;
import org.bukkit.Location;

import java.util.Collection;

public interface Hologram extends Cloneable {

    Location getLocation();

    void setLocation(Location location);

    Collection<? extends HologramLine> getLines();

    void setLines(Collection<? extends HologramLine> lines);

    Hologram clone();
}
