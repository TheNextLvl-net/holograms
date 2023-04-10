package net.thenextlvl.hologram;

import net.thenextlvl.hologram.line.HologramLine;
import org.bukkit.Location;

import java.util.Collection;

public interface HologramProvider {

    Collection<Hologram> getHolograms();

    void setHolograms(Collection<Hologram> holograms);

    Hologram createHologram(Location location, Collection<HologramLine> lines);

    HologramLine createLine(HologramLine.Type type);
}
