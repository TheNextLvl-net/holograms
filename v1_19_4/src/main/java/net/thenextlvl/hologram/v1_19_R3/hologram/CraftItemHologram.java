package net.thenextlvl.hologram.v1_19_R3.hologram;

import lombok.Getter;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.thenextlvl.hologram.api.hologram.ItemHologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftItemDisplay;
import org.bukkit.inventory.ItemStack;

@Getter
public class CraftItemHologram extends CraftItemDisplay implements ItemHologram {
    @SuppressWarnings("NotNullFieldNotInitialized")
    private Location location;

    public CraftItemHologram(Location location, ItemStack itemStack) {
        super((CraftServer) Bukkit.getServer(), new Display.ItemDisplay(EntityType.ITEM_DISPLAY,
                ((CraftWorld) location.getWorld()).getHandle()));
        setLocation(location);
        setBillboard(Billboard.CENTER);
        setItemStack(itemStack);
    }

    @Override
    public void setLocation(Location location) {
        getHandle().setPos(location.getX(), location.getY(), location.getZ());
        this.location = location;
    }
}
