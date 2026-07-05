package net.thenextlvl.hologram.plugin.dialog.input;

import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.Nullable;

public interface TextInput extends Input<TextDialogInput> {
    TextInput initial(String text);

    TextInput hideLabel(boolean hidden);

    TextInput width(@Range(from = 1, to = 1024) int width);

    TextInput maxLength(@Positive int maxLength);

    TextInput multiline(TextDialogInput.MultilineOptions options);

    TextInput maxInputLines(@Nullable @Positive Integer maxLines);

    TextInput inputHeight(@Nullable @Range(from = 1, to = 512) Integer height);
}
