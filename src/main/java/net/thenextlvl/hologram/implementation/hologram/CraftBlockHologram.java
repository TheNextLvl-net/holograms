package net.thenextlvl.hologram.implementation.hologram;

import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.thenextlvl.hologram.api.hologram.BlockHologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftBlockDisplay;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CraftBlockHologram extends CraftBlockDisplay implements BlockHologram {
    private Location location;

    public CraftBlockHologram(Location location, BlockData block) {
        super((CraftServer) Bukkit.getServer(), new Display.BlockDisplay(EntityType.BLOCK_DISPLAY,
                ((CraftWorld) location.getWorld()).getHandle()));
        setLocation(location);
        setBillboard(Billboard.CENTER);
        setBlock(block);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        getHandle().setPos(location.getX(), location.getY(), location.getZ());
        this.location = location;
    }
}
