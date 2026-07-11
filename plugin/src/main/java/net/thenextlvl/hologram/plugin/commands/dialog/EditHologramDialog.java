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
        final var back = BackButton.create(ignored -> OverviewDialog.create()).width(300);

        final var lines = hologram.getLines().toList();
        final var actions = new ArrayList<Button<?>>();
        for (var index = 0; index < lines.size(); index++) {
            final var line = lines.get(index);
            final var lineIndex = index;

            final var label = DialogSupport.lineLabel(lineIndex, line);
            final var tooltip = DialogSupport.linePreview(line, viewer);
            actions.add(DialogButton.create(audience -> {
                return EditLineDialog.create(hologram, lineIndex, audience);
            }, label).tooltip(tooltip).width(300));
        }

        actions.add(DialogButton.create(ignored -> {
            return AddLineTypeDialog.create(hologram);
        }, Component.text("Add Line", NamedTextColor.GREEN)).width(300));
        if (lines.size() > 1)
            actions.add(DialogButton.create(audience -> {
                return ChangeLineOrderDialog.create(hologram, audience);
            }, Component.text("Change order", NamedTextColor.LIGHT_PURPLE)).width(300));
        actions.add(DialogButton.create(ignored -> {
            return TeleportHologramDialog.create(hologram);
        }, Component.text("Teleport Hologram", NamedTextColor.AQUA)).width(300));
        actions.add(DialogButton.create(ignored -> {
            return RenameHologramDialog.create(hologram, hologram.getName(), null);
        }, Component.text("Rename Hologram", NamedTextColor.YELLOW)).width(300));
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
