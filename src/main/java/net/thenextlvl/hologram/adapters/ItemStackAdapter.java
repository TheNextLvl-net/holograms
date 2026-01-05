package net.thenextlvl.hologram.adapters;

import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.ByteArrayTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ItemStackAdapter implements TagAdapter<ItemStack> {
    @Override
    public ItemStack deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var bytes = tag.getAsByteArray();
        return bytes.length == 0 ? ItemStack.of(Material.AIR) : ItemStack.deserializeBytes(bytes);
    }

    @Override
    public Tag serialize(ItemStack itemStack, TagSerializationContext context) throws ParserException {
        return ByteArrayTag.of(itemStack.isEmpty() ? new byte[0] : itemStack.serializeAsBytes());
    }
}
