package net.thenextlvl.hologram.adapters.serializers;

import net.thenextlvl.hologram.line.StaticHologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import org.jspecify.annotations.NullMarked;

@NullMarked
abstract class StaticHologramLineSerializer<T extends StaticHologramLine> extends HologramLineSerializer<T> {
    @Override
    public CompoundTag serialize(final T line, final TagSerializationContext context) throws ParserException {
        final var builder = CompoundTag.builder()
                .putAll(super.serialize(line, context))
                .put("glowing", line.isGlowing());
        line.getGlowColor().map(context::serialize).ifPresent(tag -> builder.put("glowColor", tag));
        return builder.build();
    }
}
