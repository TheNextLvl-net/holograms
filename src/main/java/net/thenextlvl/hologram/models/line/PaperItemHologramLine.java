package net.thenextlvl.hologram.models.line;

import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class PaperItemHologramLine extends PaperDisplayHologramLine<ItemHologramLine, ItemDisplay> implements ItemHologramLine {
    private ItemDisplay.ItemDisplayTransform displayTransform = ItemDisplay.ItemDisplayTransform.NONE;
    private @Nullable ItemStack item = null;

    public PaperItemHologramLine(PaperHologram hologram) {
        super(hologram, ItemDisplay.class);
    }

    @Override
    public LineType getType() {
        return LineType.ITEM;
    }

    @Override
    public ItemStack getItemStack() {
        return item != null ? item.clone() : ItemType.AIR.createItemStack();
    }

    @Override
    public ItemHologramLine setItemStack(@Nullable ItemStack item) {
        this.item = item != null ? item.clone() : null;
        getEntity().ifPresent(entity -> entity.setItemStack(item));
        return this;
    }

    @Override
    public ItemDisplay.ItemDisplayTransform getItemDisplayTransform() {
        return displayTransform;
    }

    @Override
    public ItemHologramLine setItemDisplayTransform(ItemDisplay.ItemDisplayTransform display) {
        this.displayTransform = display;
        getEntity().ifPresent(entity -> entity.setItemDisplayTransform(display));
        return this;
    }

    @Override
    public double getHeight() {
        return 0.9;
    }

    @Override
    public double getOffsetBefore() {
        return 0.45;
    }

    @Override
    protected void preSpawn(ItemDisplay entity) {
        entity.setItemStack(item);
        entity.setItemDisplayTransform(displayTransform);
        super.preSpawn(entity);
    }
}
