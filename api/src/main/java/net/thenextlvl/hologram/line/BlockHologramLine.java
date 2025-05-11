package net.thenextlvl.hologram.line;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.jspecify.annotations.NullMarked;

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
