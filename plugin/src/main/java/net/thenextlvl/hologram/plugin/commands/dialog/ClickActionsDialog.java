package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

    static DialogControl create(
            final Hologram hologram,
            final HologramLine line,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, DialogLike> reopen
    ) {
        final var actions = new ArrayList<ActionButton>();
        actions.add(DialogControl.actionButton(Component.text("Add Action", NamedTextColor.GREEN),
                ignored -> SelectActionTypeDialog.create(hologram, line, header, note, reopen)));
        line.getActions().entrySet().stream()
                .sorted(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER))
                .forEach(entry -> actions.add(DialogSupport.actionButton(hologram, line, header, entry.getKey(), entry.getValue(), note, reopen)));

        final var dialog = DialogControl.create("Click Actions")
                .body(header)
                .body("Choose an action to edit or add a new one");
        if (line.getActions().isEmpty()) dialog.body("No click actions have been added yet");
        return dialog.note(note)
                .actions(actions)
                .back(reopen);
    }
}
