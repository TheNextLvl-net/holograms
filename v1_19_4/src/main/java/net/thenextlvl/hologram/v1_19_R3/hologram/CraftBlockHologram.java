package net.thenextlvl.hologram.v1_19_R3.hologram;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.thenextlvl.hologram.api.hologram.BlockHologram;
import net.thenextlvl.hologram.v1_19_R3.display.CraftEntityBlockDisplay;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftBlockDisplay;
import org.jetbrains.annotations.Nullable;

@Getter
public class CraftBlockHologram extends CraftEntityBlockDisplay implements CraftHologram, BlockHologram {
    private @Setter Location location;
    private @Nullable CraftBlockDisplay entityDisplay;

    public CraftBlockHologram(Location location, BlockData block) {
        super(block);
        this.location = location;
    }

    @Override
    public CraftBlockDisplay updateEntityDisplay() {
        var display = (CraftBlockDisplay) CraftHologram.super.updateEntityDisplay();
        display.setBlock(getBlock());
        return display;
    }

    public CraftBlockDisplay createEntityDisplay() {
        var server = (CraftServer) Bukkit.getServer();
        var world = ((CraftWorld) getLocation().getWorld()).getHandle();
        var display = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, world);
        return entityDisplay = new CraftBlockDisplay(server, display);
    }
}
