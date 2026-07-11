package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.text.Component;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
final class VisualOptionsDialog {
    private VisualOptionsDialog() {
    }

    static Dialog<?> create(
            final String title,
            final Component header,
            @Nullable final Component note,
            final List<Button<?>> actions,
            final Button<?> back
    ) {
        final var dialog = Dialog.multiAction()
                .title(Component.text(title))
                .addBody(Body.text(header))
                .addBody(Body.text(Component.text("Edit how this line looks")));
        if (note != null) dialog.addBody(Body.text(note));
        DialogSupport.addBack(actions, back).forEach(dialog::addButton);
        return dialog;
    }
}
