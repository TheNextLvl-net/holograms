package net.thenextlvl.hologram.adapters.action;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class SoundAdapter implements TagAdapter<Sound> {
    @Override
    public Sound deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var name = context.deserialize(root.get("name"), Key.class);
        final var source = context.deserialize(root.get("source"), Sound.Source.class);
        final var volume = root.get("volume").getAsFloat();
        final var pitch = root.get("pitch").getAsFloat();
        return Sound.sound(name, source, volume, pitch);
    }

    @Override
    public Tag serialize(final Sound sound, final TagSerializationContext context) throws ParserException {
        return CompoundTag.builder()
                .put("name", context.serialize(sound.name()))
                .put("source", context.serialize(sound.source()))
                .put("volume", sound.volume())
                .put("pitch", sound.pitch())
                .build();
    }
}
