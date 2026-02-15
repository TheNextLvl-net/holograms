package net.thenextlvl.hologram.adapters.action;

import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramLike;
import net.thenextlvl.hologram.HologramProvider;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.StringTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public final class HologramLikeAdapter implements TagAdapter<HologramLike> {
    @Override
    public HologramLike deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        return new SimpleHologramLike(tag.getAsString());
    }

    @Override
    public Tag serialize(final HologramLike hologram, final TagSerializationContext context) throws ParserException {
        return StringTag.of(hologram.getName());
    }

    private record SimpleHologramLike(String name) implements HologramLike {
        @Override
        public String getName() {
            return name;
        }

        @Override
        public Optional<Hologram> getHologram() {
            return HologramProvider.instance().getHologram(name);
        }
    }
}
