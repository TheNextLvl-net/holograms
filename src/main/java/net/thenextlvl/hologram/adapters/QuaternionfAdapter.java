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
    public Quaternionf deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var root = tag.getAsCompound();
        var x = root.get("x").getAsFloat();
        var y = root.get("y").getAsFloat();
        var z = root.get("z").getAsFloat();
        var w = root.get("w").getAsFloat();
        return new Quaternionf(x, y, z, w);
    }

    @Override
    public Tag serialize(Quaternionf object, TagSerializationContext context) throws ParserException {
        return CompoundTag.builder()
                .put("x", object.x())
                .put("y", object.y())
                .put("z", object.z())
                .put("w", object.w())
                .build();
    }
}
