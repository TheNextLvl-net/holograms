package net.thenextlvl.hologram.line;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftBlockDisplay;
import org.bukkit.entity.BlockDisplay;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class CraftBlockLine implements BlockLine {
    private final Consumer<BlockDisplay> consumer;

    public CraftServer getServer() {
        return (CraftServer) Bukkit.getServer();
    }

    @Override
    public CraftBlockDisplay display(Location location) {
        var level = ((CraftWorld) location.getWorld()).getHandle();
        var display = new CraftBlockDisplay(getServer(), new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level));
        consumer.accept(display);
        return display;
    }
}
