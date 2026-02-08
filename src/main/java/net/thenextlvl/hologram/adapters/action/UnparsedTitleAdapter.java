package net.thenextlvl.hologram.adapters.action;

import net.kyori.adventure.title.Title;
import net.thenextlvl.hologram.action.UnparsedTitle;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public final class UnparsedTitleAdapter implements TagAdapter<UnparsedTitle> {
    @Override
    public UnparsedTitle deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var title = root.get("title").getAsString();
        final var subtitle = root.get("subtitle").getAsString();
        final var times = root.optional("times").map(t -> context.deserialize(t, Title.Times.class)).orElse(null);
        return new UnparsedTitle(title, subtitle, times);
    }

    @Override
    public Tag serialize(final UnparsedTitle title, final TagSerializationContext context) throws ParserException {
        final var tag = CompoundTag.builder()
                .put("title", title.title())
                .put("subtitle", title.subtitle());
        Optional.ofNullable(title.times()).ifPresent(times -> tag.put("times", context.serialize(times)));
        return tag.build();
    }
}
