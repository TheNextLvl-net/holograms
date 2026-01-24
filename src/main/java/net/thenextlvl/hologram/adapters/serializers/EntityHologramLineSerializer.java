package net.thenextlvl.hologram.adapters.serializers;

import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class EntityHologramLineSerializer extends HologramLineSerializer<EntityHologramLine<?>> {
    @Override
    public CompoundTag serialize(final EntityHologramLine<?> line, final TagSerializationContext context) throws ParserException {
        return CompoundTag.builder().putAll(super.serialize(line, context))
                .put("scale", line.getScale())
                .put("offset", context.serialize(line.getOffset()))
                .put("entityType", context.serialize(line.getEntityType()))
                .build();
    }
}
