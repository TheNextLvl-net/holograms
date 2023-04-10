package net.thenextlvl.hologram.api.line;

import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;

public interface ItemLine extends HologramLine {
    @Override
    ItemDisplay display(Location location);

    @Override
    default LineType getType() {
        return LineType.ITEM;
    }
}
