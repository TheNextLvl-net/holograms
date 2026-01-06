package net.thenextlvl.hologram.adapters.serializers;

import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class BlockHologramLineSerializer extends DisplayHologramLineSerializer<BlockHologramLine> {
    @Override
    public CompoundTag serialize(BlockHologramLine line, TagSerializationContext context) throws ParserException {
        return CompoundTag.builder().putAll(super.serialize(line, context))
                .put("block", context.serialize(line.getBlock()))
                .build();
    }
}
