package net.thenextlvl.hologram.api.display;

import org.bukkit.block.data.BlockData;

public interface EntityBlockDisplay extends EntityDisplay {

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
