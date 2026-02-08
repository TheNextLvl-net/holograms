package net.thenextlvl.hologram.adapters.action;

import net.kyori.adventure.title.Title;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;

@NullMarked
public final class TitleTimesAdapter implements TagAdapter<Title.Times> {
    @Override
    public Title.Times deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var fadeIn = context.deserialize(root.get("fadeIn"), Duration.class);
        final var stay = context.deserialize(root.get("stay"), Duration.class);
        final var fadeOut = context.deserialize(root.get("fadeOut"), Duration.class);
        return Title.Times.times(fadeIn, stay, fadeOut);
    }

    @Override
    public Tag serialize(final Title.Times times, final TagSerializationContext context) throws ParserException {
        return CompoundTag.builder()
                .put("fadeIn", context.serialize(times.fadeIn()))
                .put("stay", context.serialize(times.stay()))
                .put("fadeOut", context.serialize(times.fadeOut()))
                .build();
    }
}
