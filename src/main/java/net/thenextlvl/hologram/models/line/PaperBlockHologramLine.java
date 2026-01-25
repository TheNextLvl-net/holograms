package net.thenextlvl.hologram.models.line;

import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PaperBlockHologramLine extends PaperDisplayHologramLine<BlockHologramLine, BlockDisplay> implements BlockHologramLine {
    private volatile BlockData block = BlockType.AIR.createBlockData();

    public PaperBlockHologramLine(final PaperHologram hologram) {
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
    public BlockHologramLine setBlock(final BlockData block) {
        this.block = block.clone();
        getEntities().values().forEach(entity -> entity.setBlock(block));
        return this;
    }

    @Override
    public double getHeight(final Player player) {
        return transformation.getScale().y();
    }

    @Override
    protected void preSpawn(final BlockDisplay entity, final Player player) {
        entity.setBlock(block);
        super.preSpawn(entity, player);
    }
}
