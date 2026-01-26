package net.thenextlvl.hologram.adapters.deserializers;

import net.thenextlvl.hologram.models.PaperHologram;
import net.thenextlvl.hologram.models.line.PaperStaticHologramLine;
import net.thenextlvl.hologram.models.line.PaperPagedHologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;

@NullMarked
public final class PagedHologramLineDeserializer extends HologramLineDeserializer<PaperPagedHologramLine> {
    public PagedHologramLineDeserializer(final PaperHologram hologram) {
        super(hologram);
    }

    @Override
    protected void deserialize(final PaperPagedHologramLine line, final CompoundTag tag, final TagDeserializationContext context) throws ParserException {
        tag.optional("interval").map(tag1 -> context.deserialize(tag1, Duration.class)).ifPresent(line::setInterval);
        tag.optional("randomOrder").map(Tag::getAsBoolean).ifPresent(line::setRandomOrder);
        tag.optional("paused").map(Tag::getAsBoolean).ifPresent(line::setPaused);
        tag.optional("pages").map(Tag::<CompoundTag>getAsList).ifPresent(pages -> {
            pages.stream().map(page -> PaperHologram.deserializeLine(context, page))
                    .map(PaperStaticHologramLine.class::cast)
                    .forEach(line::addPage);
        });
    }

    @Override
    protected PaperPagedHologramLine createLine(final CompoundTag tag, final TagDeserializationContext context) {
        return new PaperPagedHologramLine(hologram);
    }
}
