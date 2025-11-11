package net.thenextlvl.hologram.line;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.jspecify.annotations.NullMarked;

/**
 * Represents a line type within a hologram that displays a block.
 *
 * @since 0.1.0
 */
@NullMarked
public interface BlockHologramLine extends DisplayHologramLine<BlockDisplay> {
    /**
     * Gets the displayed block.
     *
     * @return the displayed block
     */
    BlockData getBlock();

    /**
     * Sets the displayed block.
     *
     * @param block the new block
     */
    void setBlock(BlockData block);
}
