package net.thenextlvl.hologram.api.display;

import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;

public interface EntityItemDisplay extends EntityDisplay {

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
    void setItemStack(ItemStack item);

    /**
     * Gets the item display transform for this entity.
     * Defaults to {@link ItemDisplayTransform#FIXED}.
     *
     * @return item display transform
     */
    ItemDisplayTransform getItemDisplayTransform();

    /**
     * Sets the item display transform for this entity.
     * Defaults to {@link ItemDisplayTransform#FIXED}.
     *
     * @param display new display
     */
    void setItemDisplayTransform(ItemDisplayTransform display);
}
