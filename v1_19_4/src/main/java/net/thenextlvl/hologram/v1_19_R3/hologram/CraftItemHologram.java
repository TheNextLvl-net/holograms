package net.thenextlvl.hologram.v1_19_R3.hologram;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.thenextlvl.hologram.api.hologram.ItemHologram;
import net.thenextlvl.hologram.v1_19_R3.display.CraftEntityItemDisplay;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Getter
public class CraftItemHologram extends CraftEntityItemDisplay implements CraftHologram, ItemHologram {
    private @Setter Location location;
    private @Nullable CraftItemDisplay entityDisplay;

    public CraftItemHologram(Location location, ItemStack itemStack) {
        super(itemStack);
        this.location = location;
    }

    @Override
    public CraftItemDisplay updateEntityDisplay() {
        var display = (CraftItemDisplay) CraftHologram.super.updateEntityDisplay();
        display.setItemStack(getItemStack());
        display.setItemDisplayTransform(getItemDisplayTransform());
        return display;
    }

    public CraftItemDisplay createEntityDisplay() {
        var server = (CraftServer) Bukkit.getServer();
        var world = ((CraftWorld) getLocation().getWorld()).getHandle();
        var display = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, world);
        return entityDisplay = new CraftItemDisplay(server, display);
    }
}
