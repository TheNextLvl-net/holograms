package net.thenextlvl.hologram.model.line;

import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.LineType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class PaperItemHologramLine extends PaperDisplayHologramLine<ItemDisplay> implements ItemHologramLine {
    private ItemDisplay.ItemDisplayTransform displayTransform = ItemDisplay.ItemDisplayTransform.NONE;
    private @Nullable ItemStack item = null;

    public PaperItemHologramLine(Hologram hologram) {
        super(hologram);
    }

    @Override
    public Class<ItemDisplay> getTypeClass() {
        return ItemDisplay.class;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.ITEM_DISPLAY;
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
    public void setItemStack(@Nullable ItemStack item) {
        this.item = item != null ? item.clone() : null;
        getEntity().ifPresent(entity -> entity.setItemStack(item));
    }

    @Override
    public ItemDisplay.ItemDisplayTransform getItemDisplayTransform() {
        return displayTransform;
    }

    @Override
    public void setItemDisplayTransform(ItemDisplay.ItemDisplayTransform display) {
        this.displayTransform = display;
        getEntity().ifPresent(entity -> entity.setItemDisplayTransform(display));
    }
}
