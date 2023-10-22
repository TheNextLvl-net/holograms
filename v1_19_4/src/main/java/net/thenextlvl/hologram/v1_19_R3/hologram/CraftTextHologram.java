package net.thenextlvl.hologram.v1_19_R3.hologram;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.thenextlvl.hologram.api.hologram.TextHologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftTextDisplay;

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
