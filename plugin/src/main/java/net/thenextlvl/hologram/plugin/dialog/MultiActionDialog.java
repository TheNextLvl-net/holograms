package net.thenextlvl.hologram.plugin.dialog;

import net.thenextlvl.hologram.plugin.dialog.button.Button;
import org.checkerframework.checker.index.qual.Positive;

public interface MultiActionDialog extends Dialog<MultiActionDialog> {
    MultiActionDialog exitAction(Button<?> button);

    MultiActionDialog addButton(Button<?> button);

    MultiActionDialog columns(@Positive int columns);
}
