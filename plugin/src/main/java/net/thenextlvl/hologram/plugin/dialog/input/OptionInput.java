package net.thenextlvl.hologram.plugin.dialog.input;

import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import net.kyori.adventure.text.Component;

public interface OptionInput extends Input<SingleOptionDialogInput> {
    OptionInput addOption(String key, Component title);

    OptionInput initial(String key);

    OptionInput hideLabel(boolean hidden);

    OptionInput width(int i);
}
