package net.thenextlvl.hologram.model;

import net.thenextlvl.hologram.BlockHologram;
import net.thenextlvl.hologram.HologramPlugin;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PaperBlockHologram extends PaperHologram<BlockDisplay> implements BlockHologram {
    private BlockData block = BlockType.AIR.createBlockData();

    public PaperBlockHologram(HologramPlugin plugin, String name) {
        super(plugin, name);
    }

    @Override
    public Class<BlockDisplay> getTypeClass() {
        return BlockDisplay.class;
    }

    @Override
    public EntityType getType() {
        return EntityType.BLOCK_DISPLAY;
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
