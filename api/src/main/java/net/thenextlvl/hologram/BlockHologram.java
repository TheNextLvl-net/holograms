package net.thenextlvl.hologram;

import net.thenextlvl.hologram.display.BlockHologramDisplay;
import org.bukkit.entity.BlockDisplay;
import org.jspecify.annotations.NullMarked;

/**
 * An interface that represents a hologram displaying a block
 */
@NullMarked
public interface BlockHologram extends Hologram<BlockDisplay>, BlockHologramDisplay {
}
