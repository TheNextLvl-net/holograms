package net.thenextlvl.hologram.plugin.dialog.input;

import io.papermc.paper.registry.data.dialog.input.BooleanDialogInput;

public interface BooleanInput extends Input<BooleanDialogInput> {
    BooleanInput initial(boolean initial);

    BooleanInput onFalse(String value);

    BooleanInput onTrue(String value);
}
