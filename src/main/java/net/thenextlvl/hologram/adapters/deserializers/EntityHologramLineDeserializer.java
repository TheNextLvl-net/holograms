package net.thenextlvl.hologram.adapters.deserializers;

import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import net.thenextlvl.hologram.models.line.PaperEntityHologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class EntityHologramLineDeserializer extends HologramLineDeserializer<EntityHologramLine<?>> {
    public EntityHologramLineDeserializer(PaperHologram hologram) {
        super(hologram);
    }

    @Override
    protected void deserialize(EntityHologramLine<?> line, CompoundTag tag, TagDeserializationContext context) throws ParserException {
        tag.optional("scale").map(Tag::getAsDouble).ifPresent(line::setScale);
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    protected EntityHologramLine<?> createLine(CompoundTag tag, TagDeserializationContext context) {
        var type = context.deserialize(tag.get("entityType"), EntityType.class);
        return new PaperEntityHologramLine<>(hologram, type.getEntityClass());
    }
}
