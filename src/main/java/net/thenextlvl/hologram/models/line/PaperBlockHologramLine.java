package net.thenextlvl.hologram.models.line;

import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PaperBlockHologramLine extends PaperDisplayHologramLine<BlockDisplay> implements BlockHologramLine {
    private BlockData block = BlockType.AIR.createBlockData();

    public PaperBlockHologramLine(PaperHologram hologram) {
        super(hologram, BlockDisplay.class);
    }

    @Override
    public LineType getType() {
        return LineType.BLOCK;
    }

    @Override
    public BlockData getBlock() {
        return block.clone();
    }

    @Override
    public void setBlock(BlockData block) {
        this.block = block.clone();
        getEntity().ifPresent(entity -> entity.setBlock(block));
    }

    @Override
    protected void preSpawn(BlockDisplay entity) {
        entity.setBlock(block);
        super.preSpawn(entity);
    }
}
