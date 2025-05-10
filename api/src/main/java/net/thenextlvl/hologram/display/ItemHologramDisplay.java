package net.thenextlvl.hologram.display;

import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents an item display entity.
 */
@NullMarked
public interface ItemHologramDisplay extends HologramDisplay {
    /**
     * Gets the displayed item stack.
     *
     * @return the displayed item stack
     */
    ItemStack getItemStack();

    /**
     * Sets the displayed item stack.
     *
     * @param item the new item stack
     */
    void setItemStack(@Nullable ItemStack item);

    /**
     * Gets the item display transform for this entity.
     * <p>
     * Defaults to {@link ItemDisplay.ItemDisplayTransform#FIXED}.
     *
     * @return item display transform
     */
    ItemDisplay.ItemDisplayTransform getItemDisplayTransform();

    /**
     * Sets the item display transform for this entity.
     * <p>
     * Defaults to {@link ItemDisplay.ItemDisplayTransform#FIXED}.
     *
     * @param display new display
     */
    void setItemDisplayTransform(ItemDisplay.ItemDisplayTransform display);
}
