package net.thenextlvl.hologram.v1_19_R3.line;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.thenextlvl.hologram.api.line.BlockLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftBlockDisplay;
import org.bukkit.entity.BlockDisplay;

import java.util.function.Function;

@RequiredArgsConstructor
public class CraftBlockLine extends CraftHologramLine implements BlockLine {
    private final Function<BlockDisplay, Number> function;

    @Override
    public CraftBlockDisplay display(Location location) {
        var world = (CraftWorld) location.getWorld();
        var entity = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, world.getHandle());
        var display = new CraftBlockDisplay((CraftServer) Bukkit.getServer(), entity);
        location.setY(location.getY() - function.apply(display).doubleValue());
        display.getHandle().setPos(location.getX(), location.getY(), location.getZ());
        return display;
    }
}
