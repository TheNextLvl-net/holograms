package net.thenextlvl.hologram.api.line;

import org.bukkit.Location;
import org.bukkit.entity.TextDisplay;

public interface TextLine extends HologramLine {
    @Override
    TextDisplay display(Location location);

    @Override
    default LineType getType() {
        return LineType.TEXT;
    }
}
