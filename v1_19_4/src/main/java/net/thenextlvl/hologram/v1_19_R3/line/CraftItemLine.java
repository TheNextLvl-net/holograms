package net.thenextlvl.hologram.v1_19_R3.line;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.thenextlvl.hologram.api.line.ItemLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftDisplay;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftItemDisplay;
import org.bukkit.entity.ItemDisplay;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class CraftItemLine extends CraftHologramLine implements ItemLine {
    private final Consumer<ItemDisplay> consumer;

    public CraftServer getServer() {
        return (CraftServer) Bukkit.getServer();
    }

    @Override
    public CraftItemDisplay display(Location location) {
        return display(location, display -> {
        });
    }

    @Override
    public CraftItemDisplay display(Location location, Consumer<CraftDisplay> consumer) {
        var level = ((CraftWorld) location.getWorld()).getHandle();
        var display = new CraftItemDisplay(getServer(), new Display.ItemDisplay(EntityType.ITEM_DISPLAY, level));
        this.consumer.accept(display);
        consumer.accept(display);
        display.getHandle().setPos(location.getX(), location.getY(), location.getZ());
        return display;
    }
}
