package net.thenextlvl.hologram.line;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * Represents a line type within a hologram that displays a block.
 *
 * @since 0.1.0
 */
public interface BlockHologramLine extends DisplayHologramLine<BlockDisplay> {
    /**
     * Gets the displayed block.
     *
     * @return the displayed block
     */
    @Contract(pure = true)
    BlockData getBlock();

    /**
     * Sets the displayed block.
     *
     * @param block the new block
     */
    @Contract(mutates = "this")
    void setBlock(BlockData block);
}
