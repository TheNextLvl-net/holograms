package net.thenextlvl.hologram.adapters.deserializers;

import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import net.thenextlvl.hologram.models.line.PaperBlockHologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import org.bukkit.block.data.BlockData;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class BlockHologramLineDeserializer extends DisplayHologramLineDeserializer<BlockHologramLine> {
    public BlockHologramLineDeserializer(final PaperHologram hologram) {
        super(hologram);
    }

    @Override
    protected void deserialize(final BlockHologramLine line, final CompoundTag tag, final TagDeserializationContext context) throws ParserException {
        tag.optional("block").map(tag1 -> context.deserialize(tag1, BlockData.class)).ifPresent(line::setBlock);
        super.deserialize(line, tag, context);
    }

    @Override
    protected BlockHologramLine createLine(final CompoundTag tag, final TagDeserializationContext context) {
        return new PaperBlockHologramLine(hologram, null);
    }
}
