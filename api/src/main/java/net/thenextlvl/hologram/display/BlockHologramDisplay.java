package net.thenextlvl.hologram.display;

import org.bukkit.block.data.BlockData;
import org.jspecify.annotations.NullMarked;

/**
 * Represents a block display entity.
 */
@NullMarked
public interface BlockHologramDisplay extends HologramDisplay {
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
