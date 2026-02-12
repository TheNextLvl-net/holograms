package net.thenextlvl.hologram.adapters.serializers;

import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.ListTag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PagedHologramLineSerializer extends HologramLineSerializer<PagedHologramLine> {
    @Override
    public CompoundTag serialize(final PagedHologramLine line, final TagSerializationContext context) throws ParserException {
        return CompoundTag.builder()
                .put("pages", ListTag.of(CompoundTag.ID, line.getPages()
                        .map(context::serialize)
                        .toList()))
                .put("interval", context.serialize(line.getInterval()))
                .put("randomOrder", line.isRandomOrder())
                .put("paused", line.isPaused())
                .putAll(super.serialize(line, context))
                .build();
    }
}
