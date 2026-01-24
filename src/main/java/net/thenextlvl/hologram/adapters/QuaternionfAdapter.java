package net.thenextlvl.hologram.adapters;

import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.joml.Quaternionf;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class QuaternionfAdapter implements TagAdapter<Quaternionf> {
    @Override
    public Quaternionf deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var x = root.get("x").getAsFloat();
        final var y = root.get("y").getAsFloat();
        final var z = root.get("z").getAsFloat();
        final var w = root.get("w").getAsFloat();
        return new Quaternionf(x, y, z, w);
    }

    @Override
    public Tag serialize(final Quaternionf object, final TagSerializationContext context) throws ParserException {
        return CompoundTag.builder()
                .put("x", object.x())
                .put("y", object.y())
                .put("z", object.z())
                .put("w", object.w())
                .build();
    }
}
