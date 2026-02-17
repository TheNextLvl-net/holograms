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
        if (tag.isNumber()) return new PageChange(tag.getAsInt());
        final var root = tag.getAsCompound();
        final var hologram = root.optional("hologram").map(t -> context.deserialize(t, HologramLike.class)).orElse(null);
        final var line = root.optional("line").map(Tag::getAsInt).orElse(null);
        final var page = root.get("page").getAsInt();
        return new PageChange(hologram, line, page);
    }

    @Override
    public Tag serialize(final PageChange change, final TagSerializationContext context) throws ParserException {
        final var builder = CompoundTag.builder().put("page", change.page());
        if (change.hologram() != null) builder.put("hologram", context.serialize(change.hologram()));
        if (change.line() != null) builder.put("line", change.line());
        return builder.build();
    }
}
