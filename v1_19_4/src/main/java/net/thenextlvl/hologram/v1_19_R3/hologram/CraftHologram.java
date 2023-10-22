package net.thenextlvl.hologram.v1_19_R3.hologram;

import net.thenextlvl.hologram.api.hologram.Hologram;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftDisplay;
import org.jetbrains.annotations.Nullable;

public interface CraftHologram extends Hologram {
    @Nullable CraftDisplay getEntityDisplay();

    CraftDisplay createEntityDisplay();

    default CraftDisplay updateEntityDisplay() {
        var display = getEntityDisplay() != null ? getEntityDisplay() : createEntityDisplay();
        display.setTransformation(getTransformation());
        display.setTransformationMatrix(getTransformationMatrix());
        display.setInterpolationDuration(getInterpolationDuration());
        display.setViewRange(getViewRange());
        display.setShadowRadius(getShadowRadius());
        display.setShadowStrength(getShadowStrength());
        display.setDisplayWidth(getDisplayWidth());
        display.setDisplayHeight(getDisplayHeight());
        display.setInterpolationDelay(getInterpolationDelay());
        display.setBillboard(getBillboard());
        display.setGlowColorOverride(getGlowColorOverride());
        display.setBrightness(getBrightness());
        display.setBrightness(getBrightness());
        return display;
    }
}
