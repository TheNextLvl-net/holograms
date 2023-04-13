package net.thenextlvl.hologram.api.line;

import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;

public interface BlockLine extends HologramLine {
    @Override
    default LineType getType() {
        return LineType.BLOCK;
    }
}
