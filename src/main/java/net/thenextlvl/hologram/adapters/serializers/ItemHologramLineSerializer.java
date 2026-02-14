package net.thenextlvl.hologram.adapters.serializers;

import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ItemHologramLineSerializer extends DisplayHologramLineSerializer<ItemHologramLine> {
    @Override
    public CompoundTag serialize(final ItemHologramLine line, final TagSerializationContext context) throws ParserException {
        return CompoundTag.builder().putAll(super.serialize(line, context))
                .put("itemDisplayTransform", context.serialize(line.getItemDisplayTransform()))
                .put("itemStack", context.serialize(line.getItemStack()))
                .put("playerHead", line.isPlayerHead())
                .build();
    }
}
