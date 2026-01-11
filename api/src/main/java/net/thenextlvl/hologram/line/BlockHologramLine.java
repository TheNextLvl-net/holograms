package net.thenextlvl.hologram.line;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Represents a line type within a hologram that displays a block.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface BlockHologramLine extends DisplayHologramLine<BlockHologramLine, BlockDisplay> {
    /**
     * Gets the displayed block.
     *
     * @return the displayed block
     * @since 0.1.0
     */
    @Contract(pure = true)
    BlockData getBlock();

    /**
     * Sets the displayed block.
     *
     * @param block the new block
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    BlockHologramLine setBlock(BlockData block);
}
