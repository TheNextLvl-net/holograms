package net.thenextlvl.hologram.models.line;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Material;
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
    private volatile boolean playerHead = false;

    public PaperItemHologramLine(final PaperHologram hologram, @Nullable final PaperPagedHologramLine parent) {
        super(hologram, parent, EntityType.ITEM_DISPLAY);
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
            if (!playerHead) updateItems();
        }, false);
    }

    @Override
    public boolean isPlayerHead() {
        return playerHead;
    }

    @Override
    public ItemHologramLine setPlayerHead(final boolean playerHead) {
        return set(this.playerHead, playerHead, () -> {
            this.playerHead = playerHead;
            updateItems();
        }, false);
    }

    private void updateItems() {
        if (playerHead) entities.forEach((uuid, entity) -> {
            final var head = ItemStack.of(Material.PLAYER_HEAD);
            head.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile().uuid(uuid));
            entity.setItemStack(head);
        });
        else forEachEntity(entity -> entity.setItemStack(this.item));
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
        return transformation.getScale().y();
    }

    @Override
    public double getOffsetBefore(final Player player) {
        return getHeight(player) / 2;
    }

    @Override
    public double getOffsetAfter(final Player player) {
        return -getOffsetBefore(player);
    }

    @Override
    protected void preSpawn(final ItemDisplay entity, final Player player) {
        final var item = playerHead ? ItemStack.of(Material.PLAYER_HEAD) : this.item;
        if (playerHead && item != null)
            item.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile(player.getPlayerProfile()));
        entity.setItemStack(item);
        entity.setItemDisplayTransform(displayTransform);
        super.preSpawn(entity, player);
    }
}
