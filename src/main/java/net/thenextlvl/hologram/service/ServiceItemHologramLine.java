package net.thenextlvl.hologram.service;

import net.thenextlvl.hologram.line.ItemHologramLine;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ServiceItemHologramLine extends ServiceDisplayHologramLine<ItemStack, ItemHologramLine> {
    public ServiceItemHologramLine(final ItemHologramLine line) {
        super(line);
    }

    @Override
    public ItemStack getContent() {
        return line.getItemStack();
    }

    @Override
    public void setContent(final ItemStack content) {
        line.setItemStack(content);
    }
}
