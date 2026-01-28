package net.thenextlvl.hologram.models.line;

import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class PaperItemHologramLine extends PaperDisplayHologramLine<ItemDisplay> implements ItemHologramLine {
    private volatile ItemDisplayTransform displayTransform = ItemDisplayTransform.NONE;
    private volatile @Nullable ItemStack item = null;

    public PaperItemHologramLine(final PaperHologram hologram) {
        super(hologram, EntityType.ITEM_DISPLAY);
    }

    @Override
    public LineType getType() {
        return LineType.ITEM;
    }

    @Override
    public ItemStack getItemStack() {
        final var itemStack = item;
        return itemStack != null ? itemStack.clone() : ItemType.AIR.createItemStack();
    }

    @Override
    public ItemHologramLine setItemStack(@Nullable final ItemStack item) {
        return set(this.item, item, () -> {
            this.item = item != null ? item.clone() : null;
            forEachEntity(entity -> entity.setItemStack(this.item));
        }, false);
    }

    @Override
    public ItemDisplayTransform getItemDisplayTransform() {
        return displayTransform;
    }

    @Override
    public ItemHologramLine setItemDisplayTransform(final ItemDisplayTransform display) {
        return set(this.displayTransform, display, () -> {
            this.displayTransform = display;
        }, true);
    }

    @Override
    public double getHeight(final Player player) {
        return switch (displayTransform) {
            case NONE, HEAD, GUI -> 1;
            case GROUND, THIRDPERSON_LEFTHAND, THIRDPERSON_RIGHTHAND, FIRSTPERSON_LEFTHAND, FIRSTPERSON_RIGHTHAND ->
                    0.45;
            case FIXED -> 0.5;
        } * transformation.getScale().y();
    }

    @Override
    public double getOffsetBefore(final Player player) {
        if (displayTransform == ItemDisplayTransform.GROUND) return 0;
        return getHeight(player) / 2;
    }

    @Override
    protected void preSpawn(final ItemDisplay entity, final Player player) {
        entity.setItemStack(item);
        entity.setItemDisplayTransform(displayTransform);
        super.preSpawn(entity, player);
    }
}
