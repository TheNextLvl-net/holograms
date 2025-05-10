package net.thenextlvl.hologram;

import net.thenextlvl.hologram.display.TextHologramDisplay;
import org.bukkit.entity.TextDisplay;
import org.jspecify.annotations.NullMarked;

/**
 * An interface that represents a hologram displaying a text
 */
@NullMarked
public interface TextHologram extends Hologram<TextDisplay>, TextHologramDisplay {
}
