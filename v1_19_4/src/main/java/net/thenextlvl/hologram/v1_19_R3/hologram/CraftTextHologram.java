package net.thenextlvl.hologram.v1_19_R3.hologram;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.thenextlvl.hologram.api.hologram.TextHologram;
import net.thenextlvl.hologram.v1_19_R3.display.CraftEntityTextDisplay;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftTextDisplay;
import org.jetbrains.annotations.Nullable;

@Getter
public class CraftTextHologram extends CraftEntityTextDisplay implements CraftHologram, TextHologram {
    private @Setter Location location;
    private @Nullable CraftTextDisplay entityDisplay;

    public CraftTextHologram(Location location, Component text) {
        super(text);
        this.location = location;
    }

    @Override
    public CraftTextDisplay updateEntityDisplay() {
        var display = (CraftTextDisplay) CraftHologram.super.updateEntityDisplay();
        display.text(getText());
        display.setLineWidth(getLineWidth());
        display.setBackgroundColor(getBackgroundColor());
        display.setTextOpacity(getTextOpacity());
        display.setShadowed(isShadowed());
        display.setSeeThrough(isSeeThrough());
        display.setDefaultBackground(isDefaultBackground());
        display.setAlignment(getAlignment());
        return display;
    }

    public CraftTextDisplay createEntityDisplay() {
        var server = (CraftServer) Bukkit.getServer();
        var world = ((CraftWorld) getLocation().getWorld()).getHandle();
        var display = new Display.TextDisplay(EntityType.TEXT_DISPLAY, world);
        return entityDisplay = new CraftTextDisplay(server, display);
    }
}
