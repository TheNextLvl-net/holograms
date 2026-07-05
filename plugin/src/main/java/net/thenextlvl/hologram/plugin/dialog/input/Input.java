package net.thenextlvl.hologram.plugin.dialog.input;

import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.text.Component;

public interface Input<T extends DialogInput> {
    static BooleanInput bool(final String key, final Component title) {
        return null;
    }

    static OptionInput option(final String key, final Component title) {
        return null;
    }

    static TextInput text(final String key, final Component title) {
        return null;
    }

    static SliderInput slider(final String key, final Component title, final int min, final int max) {
        return null;
    }

    T build();
}
