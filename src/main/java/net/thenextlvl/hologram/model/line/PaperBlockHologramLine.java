package net.thenextlvl.hologram.model.line;

import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.LineType;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PaperBlockHologramLine extends PaperDisplayHologramLine<BlockDisplay> implements BlockHologramLine {
    private BlockData block = BlockType.AIR.createBlockData();

    public PaperBlockHologramLine(Hologram hologram) {
        super(hologram);
    }

    @Override
    public Class<BlockDisplay> getTypeClass() {
        return BlockDisplay.class;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.BLOCK_DISPLAY;
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
}
