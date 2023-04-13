package net.thenextlvl.hologram.v1_19_R3.line;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.thenextlvl.hologram.api.line.TextLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftTextDisplay;
import org.bukkit.entity.TextDisplay;

import java.util.function.Function;

@RequiredArgsConstructor
public class CraftTextLine extends CraftHologramLine implements TextLine {
    private final Function<TextDisplay, Number> function;

    @Override
    public CraftTextDisplay display(Location location) {
        var world = (CraftWorld) location.getWorld();
        var entity = new Display.TextDisplay(EntityType.TEXT_DISPLAY, world.getHandle());
        var display = new CraftTextDisplay((CraftServer) Bukkit.getServer(), entity);
        applyDefaults(display);
        location.setY(location.getY() - function.apply(display).doubleValue());
        display.getHandle().setPos(location.getX(), location.getY(), location.getZ());
        return display;
    }

    private void applyDefaults(CraftTextDisplay display) {
        display.setBillboard(org.bukkit.entity.Display.Billboard.CENTER);
    }
}
