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

import java.util.Objects;

@NullMarked
public final class PaperItemHologramLine extends PaperDisplayHologramLine<ItemDisplay> implements ItemHologramLine {
    private volatile ItemDisplayTransform displayTransform = ItemDisplayTransform.FIXED;
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
            if (item != null) this.playerHead = false;
            this.item = item != null ? item.clone() : null;
            forEachEntity(entity -> entity.setItemStack(this.item));
        }, false);
    }

    @Override
    public boolean isPlayerHead() {
        return playerHead;
    }

    @Override
    public ItemHologramLine setPlayerHead(final boolean playerHead) {
        if (Objects.equals(this.playerHead, playerHead)) return this;
        this.playerHead = playerHead;
        if (playerHead) this.item = ItemStack.of(Material.PLAYER_HEAD);
        if (playerHead) entities.forEach((uuid, entity) -> {
            final var head = ItemStack.of(Material.PLAYER_HEAD);
            head.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile().uuid(uuid));
            entity.setItemStack(head);
        });
        else forEachEntity(entity -> entity.setItemStack(this.item));
        return this;
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
        final var item = playerHead ? ItemStack.of(Material.PLAYER_HEAD) : this.item;
        if (playerHead && item != null)
            item.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile(player.getPlayerProfile()));
        entity.setItemStack(item);
        entity.setItemDisplayTransform(displayTransform);
        super.preSpawn(entity, player);
    }
}
