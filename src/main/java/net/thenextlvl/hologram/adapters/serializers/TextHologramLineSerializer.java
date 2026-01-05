package net.thenextlvl.hologram.adapters.serializers;

import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class TextHologramLineSerializer extends DisplayHologramLineSerializer<TextHologramLine> {
    @Override
    public CompoundTag serialize(TextHologramLine line, TagSerializationContext context) throws ParserException {
        var builder = CompoundTag.builder();
        line.getText().map(context::serialize).ifPresent(tag -> builder.put("text", tag));
        return builder.putAll(super.serialize(line, context)).build();
    }   
}
