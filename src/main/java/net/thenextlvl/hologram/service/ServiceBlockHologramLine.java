package net.thenextlvl.hologram.service;

import net.thenextlvl.hologram.line.BlockHologramLine;
import org.bukkit.block.data.BlockData;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ServiceBlockHologramLine extends ServiceDisplayHologramLine<BlockHologramLine> implements net.thenextlvl.service.hologram.line.BlockHologramLine {
    public ServiceBlockHologramLine(final ServiceHologram hologram, final BlockHologramLine line) {
        super(hologram, line);
    }

    @Override
    public BlockData getBlock() {
        return line.getBlock();
    }

    @Override
    public boolean setBlock(final BlockData block) {
        return line.setBlock(block);
    }
}
