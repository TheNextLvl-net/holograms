package net.thenextlvl.hologram.adapters.deserializers;

import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import net.thenextlvl.hologram.models.line.PaperEntityHologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.entity.EntityType;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class EntityHologramLineDeserializer extends HologramLineDeserializer<EntityHologramLine<?>> {
    public EntityHologramLineDeserializer(final PaperHologram hologram) {
        super(hologram);
    }

    @Override
    protected void deserialize(final EntityHologramLine<?> line, final CompoundTag tag, final TagDeserializationContext context) throws ParserException {
        tag.optional("scale").map(Tag::getAsDouble).ifPresent(line::setScale);
        tag.optional("offset").map(tag1 -> context.deserialize(tag1, Vector3f.class)).ifPresent(line::setOffset);
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    protected EntityHologramLine<?> createLine(final CompoundTag tag, final TagDeserializationContext context) {
        final var type = context.deserialize(tag.get("entityType"), EntityType.class);
        return new PaperEntityHologramLine<>(hologram, type.getEntityClass());
    }
}
