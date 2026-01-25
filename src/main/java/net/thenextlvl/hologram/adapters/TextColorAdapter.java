package net.thenextlvl.hologram.adapters;

import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.IntTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class TextColorAdapter implements TagAdapter<TextColor> {
    @Override
    public TextColor deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        return TextColor.color(tag.getAsInt());
    }

    @Override
    public Tag serialize(final TextColor object, final TagSerializationContext context) throws ParserException {
        return IntTag.of(object.value());
    }
}