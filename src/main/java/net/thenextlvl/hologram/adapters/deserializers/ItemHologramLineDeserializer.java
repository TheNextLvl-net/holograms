package net.thenextlvl.hologram.adapters.deserializers;

import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import net.thenextlvl.hologram.models.line.PaperItemHologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ItemHologramLineDeserializer extends DisplayHologramLineDeserializer<ItemHologramLine> {
    public ItemHologramLineDeserializer(PaperHologram hologram) {
        super(hologram);
    }

    @Override
    protected void deserialize(ItemHologramLine line, CompoundTag tag, TagDeserializationContext context) throws ParserException {
        tag.optional("itemStack").map(tag1 -> context.deserialize(tag1, ItemStack.class)).ifPresent(line::setItemStack);
        tag.optional("itemDisplayTransform").map(tag1 -> context.deserialize(tag1, ItemDisplayTransform.class)).ifPresent(line::setItemDisplayTransform);
        super.deserialize(line, tag, context);
    }

    @Override
    protected ItemHologramLine createLine(CompoundTag tag, TagDeserializationContext context) {
        return new PaperItemHologramLine(hologram);
    }
}