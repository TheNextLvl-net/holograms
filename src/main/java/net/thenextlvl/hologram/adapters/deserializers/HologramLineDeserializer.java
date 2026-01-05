package net.thenextlvl.hologram.adapters.deserializers;

import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagDeserializer;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
abstract class HologramLineDeserializer<T extends HologramLine<?>> implements TagDeserializer<T> {
    @Override
    public T deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return null;
    }
}
