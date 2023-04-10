package net.thenextlvl.hologram.line;

import org.bukkit.entity.Display;

public interface HologramLine {

    Type getType();

    Display getDisplay();

    enum Type {
        TEXT, ITEM, BLOCK
    }
}
