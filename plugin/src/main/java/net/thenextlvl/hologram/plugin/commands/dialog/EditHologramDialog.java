package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;

@NullMarked
final class EditHologramDialog {
    private EditHologramDialog() {
    }

    static Dialog<?> create(final Hologram hologram, final Audience viewer) {
        final var back = BackButton.create(300, ignored -> OverviewDialog.create());

        final var lines = hologram.getLines().toList();
        final var actions = new ArrayList<Button<?>>();
        for (var index = 0; index < lines.size(); index++) {
            final var line = lines.get(index);
            final var lineIndex = index;
            actions.add(DialogButton.create(DialogSupport.lineLabel(lineIndex, line), audience -> EditLineDialog.create(hologram, lineIndex, audience))
                    .tooltip(DialogSupport.linePreview(line, viewer)).width(300));
        }

        actions.add(DialogButton.create(Component.text("Add Line", NamedTextColor.GREEN), 300,
                ignored -> AddLineTypeDialog.create(hologram)));
        if (lines.size() > 1)
            actions.add(DialogButton.create(Component.text("Change order", NamedTextColor.LIGHT_PURPLE), 300,
                    audience -> ChangeLineOrderDialog.create(hologram, audience)));
        actions.add(DialogButton.create(Component.text("Teleport Hologram", NamedTextColor.AQUA), 300,
                ignored -> TeleportHologramDialog.create(hologram)));
        actions.add(DialogButton.create(Component.text("Rename Hologram", NamedTextColor.YELLOW), 300,
                ignored -> RenameHologramDialog.create(hologram, hologram.getName(), null)));
        actions.add(Button.clickEvent(ClickEvent.callback(audience ->
                audience.showDialog(DeleteHologramDialog.create(hologram, audience).build())), Component.text("Delete Hologram", NamedTextColor.RED)).width(300));

        actions.add(back);

        final var dialog = Dialog.multiAction().title(Component.text(hologram.getName())).columns(1);
        actions.forEach(dialog::addButton);
        if (lines.isEmpty()) dialog.addBody(Body.text(Component.text("No lines have been added yet")));
        return dialog;
    }

    static Dialog<?> create(final Hologram hologram) {
        return EditHologramDialog.create(hologram, Audience.empty());
    }
}
