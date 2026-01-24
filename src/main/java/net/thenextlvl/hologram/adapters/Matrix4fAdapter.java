package net.thenextlvl.hologram.adapters;

import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.joml.Matrix4f;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Matrix4fAdapter implements TagAdapter<Matrix4f> {
    @Override
    public Matrix4f deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        return new Matrix4f(
                root.get("m00").getAsFloat(),
                root.get("m01").getAsFloat(),
                root.get("m02").getAsFloat(),
                root.get("m03").getAsFloat(),
                root.get("m10").getAsFloat(),
                root.get("m11").getAsFloat(),
                root.get("m12").getAsFloat(),
                root.get("m13").getAsFloat(),
                root.get("m20").getAsFloat(),
                root.get("m21").getAsFloat(),
                root.get("m22").getAsFloat(),
                root.get("m23").getAsFloat(),
                root.get("m30").getAsFloat(),
                root.get("m31").getAsFloat(),
                root.get("m32").getAsFloat(),
                root.get("m33").getAsFloat()
        );
    }

    @Override
    public CompoundTag serialize(final Matrix4f matrix4f, final TagSerializationContext context) {
        return CompoundTag.builder()
                .put("m00", matrix4f.m00())
                .put("m01", matrix4f.m01())
                .put("m02", matrix4f.m02())
                .put("m03", matrix4f.m03())
                .put("m10", matrix4f.m10())
                .put("m11", matrix4f.m11())
                .put("m12", matrix4f.m12())
                .put("m13", matrix4f.m13())
                .put("m20", matrix4f.m20())
                .put("m21", matrix4f.m21())
                .put("m22", matrix4f.m22())
                .put("m23", matrix4f.m23())
                .put("m30", matrix4f.m30())
                .put("m31", matrix4f.m31())
                .put("m32", matrix4f.m32())
                .put("m33", matrix4f.m33())
                .build();
    }
}
