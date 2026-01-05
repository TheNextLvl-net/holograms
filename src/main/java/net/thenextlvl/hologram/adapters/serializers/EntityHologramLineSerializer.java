package net.thenextlvl.hologram.adapters.serializers;

import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.serialization.TagSerializer;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class EntityHologramLineSerializer implements TagSerializer<EntityHologramLine<?>> {
    @Override
    public Tag serialize(EntityHologramLine<?> line, TagSerializationContext context) throws ParserException {
        return CompoundTag.builder()
                .put("scale", line.getScale())
                .build();
    }
}
