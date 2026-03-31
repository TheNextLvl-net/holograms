package net.thenextlvl.hologram.service;

import net.thenextlvl.hologram.line.ItemHologramLine;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class ServiceItemHologramLine extends ServiceDisplayHologramLine<ItemHologramLine> implements net.thenextlvl.service.api.hologram.line.ItemHologramLine {
    public ServiceItemHologramLine(final ServiceHologram hologram, final ItemHologramLine line) {
        super(hologram, line);
    }

    @Override
    public ItemStack getItemStack() {
        return line.getItemStack();
    }

    @Override
    public boolean setItemStack(@Nullable final ItemStack item) {
        return line.setItemStack(item);
    }

    @Override
    public boolean isPlayerHead() {
        return line.isPlayerHead();
    }

    @Override
    public boolean setPlayerHead(final boolean playerHead) {
        return line.setPlayerHead(playerHead);
    }

    @Override
    public ItemDisplay.ItemDisplayTransform getItemDisplayTransform() {
        return line.getItemDisplayTransform();
    }

    @Override
    public boolean setItemDisplayTransform(final ItemDisplay.ItemDisplayTransform display) {
        return line.setItemDisplayTransform(display);
    }
}
