package net.thenextlvl.hologram.adapters;

import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class TransformationAdapter implements TagAdapter<Transformation> {
    @Override
    public Transformation deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var root = tag.getAsCompound();
        var translation = context.deserialize(root.get("translation"), Vector3f.class);
        var leftRotation = context.deserialize(root.get("leftRotation"), Quaternionf.class);
        var scale = context.deserialize(root.get("scale"), Vector3f.class);
        var rightRotation = context.deserialize(root.get("rightRotation"), Quaternionf.class);
        return new Transformation(translation, leftRotation, scale, rightRotation);
    }

    @Override
    public Tag serialize(Transformation transformation, TagSerializationContext context) throws ParserException {
        return CompoundTag.builder()
                .put("translation", context.serialize(transformation.getTranslation()))
                .put("leftRotation", context.serialize(transformation.getLeftRotation()))
                .put("scale", context.serialize(transformation.getScale()))
                .put("rightRotation", context.serialize(transformation.getRightRotation()))
                .build();
    }
}
