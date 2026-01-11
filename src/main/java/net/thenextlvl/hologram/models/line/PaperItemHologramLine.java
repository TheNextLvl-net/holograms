package net.thenextlvl.hologram.models.line;

import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class PaperItemHologramLine extends PaperDisplayHologramLine<ItemHologramLine, ItemDisplay> implements ItemHologramLine {
    private ItemDisplayTransform displayTransform = ItemDisplayTransform.NONE;
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
    public ItemDisplayTransform getItemDisplayTransform() {
        return displayTransform;
    }

    @Override
    public ItemHologramLine setItemDisplayTransform(ItemDisplayTransform display) {
        if (Objects.equals(this.displayTransform, display)) return this;
        this.displayTransform = display;
        getEntity().ifPresent(entity -> entity.setItemDisplayTransform(display));
        getHologram().updateHologram();
        return this;
    }

    @Override
    public double getHeight() {
        return switch (displayTransform) {
            case NONE, HEAD, GUI -> 1;
            case GROUND, THIRDPERSON_LEFTHAND, THIRDPERSON_RIGHTHAND, FIRSTPERSON_LEFTHAND, FIRSTPERSON_RIGHTHAND ->
                    0.45;
            case FIXED -> 0.5;
        } * transformation.getScale().y();
    }

    @Override
    public double getOffsetBefore() {
        if (displayTransform == ItemDisplayTransform.GROUND) return 0;
        return getHeight() / 2;
    }

    @Override
    protected void preSpawn(ItemDisplay entity) {
        entity.setItemStack(item);
        entity.setItemDisplayTransform(displayTransform);
        super.preSpawn(entity);
    }
}
