package net.thenextlvl.hologram.v1_20_R1.hologram;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.thenextlvl.hologram.api.hologram.ItemHologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftItemDisplay;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class CraftItemHologram extends CraftItemDisplay implements ItemHologram {
    private Location location;

    public CraftItemHologram(Location location, ItemStack itemStack) {
        super((CraftServer) Bukkit.getServer(), new Display.ItemDisplay(EntityType.ITEM_DISPLAY,
                ((CraftWorld) location.getWorld()).getHandle()));
        setBillboard(Billboard.CENTER);
        setItemStack(itemStack);
        this.location = location;
    }
}
