package net.thenextlvl.hologram.v1_19_R3.line;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.thenextlvl.hologram.api.line.ItemLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftItemDisplay;
import org.bukkit.entity.ItemDisplay;

import java.util.function.Function;

@RequiredArgsConstructor
public class CraftItemLine extends CraftHologramLine implements ItemLine {
    private final Function<ItemDisplay, Number> function;

    @Override
    public CraftItemDisplay display(Location location) {
        var world = (CraftWorld) location.getWorld();
        var entity = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, world.getHandle());
        var display = new CraftItemDisplay((CraftServer) Bukkit.getServer(), entity);
        applyDefaults(display);
        location.setY(location.getY() - function.apply(display).doubleValue());
        display.getHandle().setPos(location.getX(), location.getY(), location.getZ());
        return display;
    }

    private void applyDefaults(CraftItemDisplay display) {
        display.setBillboard(org.bukkit.entity.Display.Billboard.CENTER);
    }
}
