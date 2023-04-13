package net.thenextlvl.hologram.api.line;

import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;

public interface ItemLine extends HologramLine {
    @Override
    default LineType getType() {
        return LineType.ITEM;
    }
}
