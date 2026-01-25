package net.thenextlvl.hologram.adapters.serializers;

import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class TextHologramLineSerializer extends DisplayHologramLineSerializer<TextHologramLine> {
    @Override
    public CompoundTag serialize(final TextHologramLine line, final TagSerializationContext context) throws ParserException {
        final var builder = CompoundTag.builder();
        line.getUnparsedText().ifPresent(tag -> builder.put("text", tag));
        line.getBackgroundColor().map(context::serialize).ifPresent(color -> builder.put("backgroundColor", color));
        return builder.putAll(super.serialize(line, context))
                .put("lineWidth", line.getLineWidth())
                .put("textOpacity", line.getTextOpacity())
                .put("shadowed", line.isShadowed())
                .put("seeThrough", line.isSeeThrough())
                .put("defaultBackground", line.isDefaultBackground())
                .put("alignment", context.serialize(line.getAlignment()))
                .build();
    }
}
