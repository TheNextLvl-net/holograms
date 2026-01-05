package net.thenextlvl.hologram.adapters.serializers;

import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.serialization.TagSerializer;
import net.thenextlvl.nbt.tag.CompoundTag;
import org.jspecify.annotations.NullMarked;

@NullMarked
abstract class HologramLineSerializer<T extends HologramLine<?>> implements TagSerializer<T> {
    @Override
    public CompoundTag serialize(T object, TagSerializationContext context) throws ParserException {
        return CompoundTag.builder()
                .put("lineType", context.serialize(object.getType()))
                .build();
    }
}
