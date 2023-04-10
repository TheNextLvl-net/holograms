package net.thenextlvl.hologram.line;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftTextDisplay;
import org.bukkit.entity.TextDisplay;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class CraftTextLine implements TextLine {
    private final Consumer<TextDisplay> consumer;

    public CraftServer getServer() {
        return (CraftServer) Bukkit.getServer();
    }

    @Override
    public CraftTextDisplay display(Location location) {
        var level = ((CraftWorld) location.getWorld()).getHandle();
        var display = new CraftTextDisplay(getServer(), new Display.TextDisplay(EntityType.TEXT_DISPLAY, level));
        consumer.accept(display);
        return display;
    }
}
