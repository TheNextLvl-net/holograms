package net.thenextlvl.hologram.api.line;

import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;

public interface BlockLine extends HologramLine {
    @Override
    BlockDisplay display(Location location);

    @Override
    default LineType getType() {
        return LineType.BLOCK;
    }
}
