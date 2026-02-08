package net.thenextlvl.hologram.adapters.deserializers;

import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.hologram.line.StaticHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
abstract class StaticHologramLineDeserializer<T extends StaticHologramLine> extends HologramLineDeserializer<T> {
    protected StaticHologramLineDeserializer(final PaperHologram hologram) {
        super(hologram);
    }

    @Override
    protected void deserialize(final T line, final CompoundTag tag, final TagDeserializationContext context) throws ParserException {
        super.deserialize(line, tag, context);
        tag.optional("glowing").map(Tag::getAsBoolean).ifPresent(line::setGlowing);
        tag.optional("glowColor").map(tag1 -> context.deserialize(tag1, TextColor.class)).ifPresent(line::setGlowColor);
    }
}
