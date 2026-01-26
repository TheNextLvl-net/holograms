package net.thenextlvl.hologram.adapters.serializers;

import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.serialization.TagSerializer;
import net.thenextlvl.nbt.tag.CompoundTag;
import org.jspecify.annotations.NullMarked;

@NullMarked
abstract class HologramLineSerializer<T extends HologramLine> implements TagSerializer<T> {
    @Override
    public CompoundTag serialize(final T line, final TagSerializationContext context) throws ParserException {
        final var builder = CompoundTag.builder()
                .put("glowing", line.isGlowing())
                .put("lineType", context.serialize(line.getType()));
        line.getGlowColor().map(context::serialize).ifPresent(tag -> builder.put("glowColor", tag));
        return builder.build();
    }
}
