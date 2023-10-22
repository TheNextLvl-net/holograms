package net.thenextlvl.hologram.v1_19_R3.display;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.thenextlvl.hologram.api.display.EntityTextDisplay;
import org.bukkit.Color;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@RequiredArgsConstructor
public class CraftEntityTextDisplay extends CraftEntityDisplay implements EntityTextDisplay {
    private @NotNull Component text;
    private int lineWidth;
    private @Nullable Color backgroundColor;
    private byte textOpacity;
    private boolean shadowed;
    private boolean seeThrough;
    private boolean defaultBackground;
    private TextAlignment alignment = TextAlignment.CENTER;
}
