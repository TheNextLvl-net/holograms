package net.thenextlvl.hologram.implementation.hologram;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.thenextlvl.hologram.api.hologram.TextHologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftTextDisplay;

@Getter
public class CraftTextHologram extends CraftTextDisplay implements TextHologram {
    @SuppressWarnings("NotNullFieldNotInitialized")
    private Location location;

    public CraftTextHologram(Location location, Component text) {
        super((CraftServer) Bukkit.getServer(), new Display.TextDisplay(EntityType.TEXT_DISPLAY,
                ((CraftWorld) location.getWorld()).getHandle()));
        setLocation(location);
        setBillboard(Billboard.CENTER);
        text(text);
    }

    @Override
    public void setLocation(Location location) {
        getHandle().setPos(location.getX(), location.getY(), location.getZ());
        this.location = location;
    }
}
