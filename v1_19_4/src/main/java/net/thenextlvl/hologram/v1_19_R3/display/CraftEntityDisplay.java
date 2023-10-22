package net.thenextlvl.hologram.v1_19_R3.display;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.hologram.api.display.EntityDisplay;
import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.util.Transformation;
import org.joml.Matrix4f;

@Getter
@Setter
public class CraftEntityDisplay implements EntityDisplay {
    private Transformation transformation;
    private Matrix4f transformationMatrix;
    private int interpolationDuration;
    private float viewRange;
    private float shadowRadius;
    private float shadowStrength;
    private float displayWidth;
    private float displayHeight;
    private int interpolationDelay;
    private Billboard billboard = Billboard.CENTER;
    private Color glowColorOverride;
    private Brightness brightness;
}
