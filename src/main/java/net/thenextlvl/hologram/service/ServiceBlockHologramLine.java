package net.thenextlvl.hologram.service;

import net.thenextlvl.hologram.line.BlockHologramLine;
import org.bukkit.block.data.BlockData;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ServiceBlockHologramLine extends ServiceDisplayHologramLine<BlockData, BlockHologramLine> {
    public ServiceBlockHologramLine(final BlockHologramLine line) {
        super(line);
    }

    @Override
    public BlockData getContent() {
        return line.getBlock();
    }

    @Override
    public void setContent(final BlockData content) {
        line.setBlock(content);
    }
}
