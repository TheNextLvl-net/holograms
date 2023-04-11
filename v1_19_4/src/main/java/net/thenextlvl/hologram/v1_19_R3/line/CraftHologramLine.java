package net.thenextlvl.hologram.v1_19_R3.line;

import net.thenextlvl.hologram.api.line.HologramLine;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftDisplay;

import java.util.function.Consumer;

public abstract class CraftHologramLine implements HologramLine {
    @Override
    public abstract CraftDisplay display(Location location);

    public abstract <T extends CraftDisplay> CraftDisplay display(Location location, Consumer<T> consumer);
}
