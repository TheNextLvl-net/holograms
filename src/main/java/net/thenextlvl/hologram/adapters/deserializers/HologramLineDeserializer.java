package net.thenextlvl.hologram.adapters.deserializers;

import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagDeserializer;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
abstract class HologramLineDeserializer<T extends HologramLine> implements TagDeserializer<T> {
    protected final PaperHologram hologram;

    protected HologramLineDeserializer(final PaperHologram hologram) {
        this.hologram = hologram;
    }

    protected void deserialize(final T line, final CompoundTag tag, final TagDeserializationContext context) throws ParserException {
        tag.optional("clickActions").map(Tag::getAsCompound).ifPresent(actions -> actions.forEach((name, action) ->
                line.addAction(name, context.deserialize(action, ClickAction.class))));
        tag.optional("viewPermission").map(Tag::getAsString).ifPresent(line::setViewPermission);
    }

    protected abstract T createLine(CompoundTag tag, TagDeserializationContext context);

    @Override
    public final T deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var line = createLine(root, context);
        deserialize(line, root, context);
        return line;
    }
}
