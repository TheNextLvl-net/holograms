package net.thenextlvl.hologram.v1_20_R3.hologram;

import lombok.Getter;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.thenextlvl.hologram.api.hologram.BlockHologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftBlockDisplay;

@Getter
public class CraftBlockHologram extends CraftBlockDisplay implements BlockHologram {
    @SuppressWarnings("NotNullFieldNotInitialized")
    private Location location;

    public CraftBlockHologram(Location location, BlockData block) {
        super((CraftServer) Bukkit.getServer(), new Display.BlockDisplay(EntityType.BLOCK_DISPLAY,
                ((CraftWorld) location.getWorld()).getHandle()));
        setLocation(location);
        setBillboard(Billboard.CENTER);
        setBlock(block);
    }

    @Override
    public void setLocation(Location location) {
        getHandle().setPos(location.getX(), location.getY(), location.getZ());
        this.location = location;
    }
}
