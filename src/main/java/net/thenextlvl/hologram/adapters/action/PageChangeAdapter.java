package net.thenextlvl.hologram.adapters.action;

import net.thenextlvl.hologram.HologramLike;
import net.thenextlvl.hologram.action.PageChange;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PageChangeAdapter implements TagAdapter<PageChange> {
    @Override
    public PageChange deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var hologram = context.deserialize(root.get("hologram"), HologramLike.class);
        final var line = root.get("line").getAsInt();
        final var page = root.get("page").getAsInt();
        return new PageChange(hologram, line, page);
    }

    @Override
    public Tag serialize(final PageChange change, final TagSerializationContext context) throws ParserException {
        return CompoundTag.builder()
                .put("hologram", context.serialize(change.hologram()))
                .put("line", change.line())
                .put("page", change.page())
                .build();
    }
}
