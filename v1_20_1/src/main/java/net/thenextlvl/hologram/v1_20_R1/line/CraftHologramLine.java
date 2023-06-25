package net.thenextlvl.hologram.v1_20_R1.line;

import net.thenextlvl.hologram.api.line.HologramLine;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftDisplay;

public abstract class CraftHologramLine implements HologramLine {
    public abstract CraftDisplay display(Location location);
}
