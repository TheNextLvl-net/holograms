package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.HologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

@NullMarked
final class ClickActionsDialog {
    private ClickActionsDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final HologramLine line,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, Dialog<?>> reopen
    ) {
        final var actions = new ArrayList<Button<?>>();
        actions.add(DialogButton.create(Component.text("Add Action", NamedTextColor.GREEN),
                ignored -> SelectActionTypeDialog.create(hologram, line, header, note, reopen)));
        line.getActions().entrySet().stream()
                .sorted(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER))
                .forEach(entry -> actions.add(DialogSupport.actionButton(hologram, line, header, entry.getKey(), entry.getValue(), note, reopen)));

        final var dialog = Dialog.multiAction().title(Component.text("Click Actions"))
                .addBody(Body.text(header))
                .addBody(Body.text(Component.text("Choose an action to edit or add a new one")));
        if (line.getActions().isEmpty())
            dialog.addBody(Body.text(Component.text("No click actions have been added yet")));
        if (note != null) dialog.addBody(Body.text(note));
        actions.forEach(dialog::addButton);
        return dialog.exitAction(BackButton.create(reopen));
    }
}
