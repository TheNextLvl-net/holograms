package net.thenextlvl.hologram.plugin.dialog.input;

import io.papermc.paper.registry.data.dialog.input.NumberRangeDialogInput;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.Nullable;

public interface SliderInput extends Input<NumberRangeDialogInput> {
    SliderInput initial(@Nullable Float initial);

    SliderInput labelFormat(String format);

    SliderInput step(@Positive @Nullable Float step);

    SliderInput width(@Range(from = 1, to = 1024) int width);
}
