package net.thenextlvl.hologram.v1_19_R3.hologram;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.thenextlvl.hologram.api.hologram.BlockHologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftBlockDisplay;

@Getter
@Setter
public class CraftBlockHologram extends CraftBlockDisplay implements BlockHologram {
    private Location location;

    public CraftBlockHologram(Location location, BlockData block) {
        super((CraftServer) Bukkit.getServer(), new Display.BlockDisplay(EntityType.BLOCK_DISPLAY,
                ((CraftWorld) location.getWorld()).getHandle()));
        setBillboard(Billboard.CENTER);
        setBlock(block);
        this.location = location;
    }
}
