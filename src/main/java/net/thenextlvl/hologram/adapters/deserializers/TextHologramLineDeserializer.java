package net.thenextlvl.hologram.adapters.deserializers;

import net.thenextlvl.hologram.models.PaperHologram;
import net.thenextlvl.hologram.models.line.PaperTextHologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.Color;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class TextHologramLineDeserializer extends DisplayHologramLineDeserializer<PaperTextHologramLine> {
    public TextHologramLineDeserializer(final PaperHologram hologram) {
        super(hologram);
    }

    @Override
    protected void deserialize(final PaperTextHologramLine line, final CompoundTag tag, final TagDeserializationContext context) throws ParserException {
        tag.optional("text").map(Tag::getAsString).ifPresent(line::setUnparsedText);
        tag.optional("lineWidth").map(Tag::getAsInt).ifPresent(line::setLineWidth);
        tag.optional("backgroundColor").map(tag1 -> context.deserialize(tag1, Color.class)).ifPresent(line::setBackgroundColor);
        tag.optional("textOpacity").map(Tag::getAsFloat).ifPresent(line::setTextOpacity);
        tag.optional("shadowed").map(Tag::getAsBoolean).ifPresent(line::setShadowed);
        tag.optional("seeThrough").map(Tag::getAsBoolean).ifPresent(line::setSeeThrough);
        tag.optional("defaultBackground").map(Tag::getAsBoolean).ifPresent(line::setDefaultBackground);
        tag.optional("alignment").map(tag1 -> context.deserialize(tag1, TextAlignment.class)).ifPresent(line::setAlignment);
        super.deserialize(line, tag, context);
    }

    @Override
    protected PaperTextHologramLine createLine(final CompoundTag tag, final TagDeserializationContext context) {
        return new PaperTextHologramLine(hologram, null);
    }
}
