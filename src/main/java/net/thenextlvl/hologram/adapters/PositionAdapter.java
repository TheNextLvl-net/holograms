package net.thenextlvl.hologram.adapters;

import io.papermc.paper.math.Position;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PositionAdapter implements TagAdapter<Position> {
    @Override
    public Position deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var x = root.get("x").getAsDouble();
        final var y = root.get("y").getAsDouble();
        final var z = root.get("z").getAsDouble();
        return Position.fine(x, y, z);
    }

    @Override
    public Tag serialize(final Position position, final TagSerializationContext context) throws ParserException {
        return CompoundTag.builder()
                .put("x", position.x())
                .put("y", position.y())
                .put("z", position.z())
                .build();
    }
}
