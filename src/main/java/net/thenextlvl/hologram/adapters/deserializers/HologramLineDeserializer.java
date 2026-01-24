package net.thenextlvl.hologram.adapters.deserializers;

import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagDeserializer;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
abstract class HologramLineDeserializer<T extends HologramLine<?>> implements TagDeserializer<T> {
    protected final PaperHologram hologram;

    protected HologramLineDeserializer(final PaperHologram hologram) {
        this.hologram = hologram;
    }

    protected abstract void deserialize(T line, CompoundTag tag, TagDeserializationContext context) throws ParserException;

    protected abstract T createLine(CompoundTag tag, TagDeserializationContext context);

    @Override
    public final T deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var line = createLine(root, context);
        deserialize(line, root, context);
        return line;
    }
}
