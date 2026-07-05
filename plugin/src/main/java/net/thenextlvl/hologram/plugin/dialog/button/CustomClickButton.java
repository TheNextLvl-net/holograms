package net.thenextlvl.hologram.plugin.dialog.button;

import org.jspecify.annotations.Nullable;

import java.time.temporal.TemporalAmount;

public interface CustomClickButton extends Button<CustomClickButton> {
    CustomClickButton lifetime(@Nullable TemporalAmount lifetime);

    CustomClickButton uses(@Nullable Integer uses);
}
