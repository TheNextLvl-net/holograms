package net.thenextlvl.hologram.line;

import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Represents a line type within a hologram that displays a block.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface BlockHologramLine extends DisplayHologramLine {
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
     * @return {@code true} if the block was successfully set, {@code false} otherwise
     * @since 1.0.0
     */
    @Contract(mutates = "this")
    boolean setBlock(BlockData block);
}
