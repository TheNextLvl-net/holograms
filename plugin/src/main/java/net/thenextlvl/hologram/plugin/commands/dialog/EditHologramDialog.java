package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;

@NullMarked
final class EditHologramDialog {
    private EditHologramDialog() {
    }

    static DialogControl create(final Hologram hologram, final Audience viewer) {
        final var back = DialogControl.backButton(300, ignored -> OverviewDialog.create());

        final var lines = hologram.getLines().toList();
        final var actions = new ArrayList<ActionButton>();
        for (var index = 0; index < lines.size(); index++) {
            final var line = lines.get(index);
            final var lineIndex = index;
            actions.add(ActionButton.builder(DialogSupport.lineLabel(lineIndex, line))
                    .tooltip(DialogSupport.linePreview(line, viewer))
                    .action(DialogControl.open(audience -> EditLineDialog.create(hologram, lineIndex, audience))).width(300).build());
        }

        actions.add(DialogControl.actionButton(Component.text("Add Line", NamedTextColor.GREEN), 300,
                ignored -> AddLineTypeDialog.create(hologram)));
        if (lines.size() > 1)
            actions.add(DialogControl.actionButton(Component.text("Change order", NamedTextColor.LIGHT_PURPLE), 300,
                    audience -> ChangeLineOrderDialog.create(hologram, audience)));
        actions.add(DialogControl.actionButton(Component.text("Teleport Hologram", NamedTextColor.AQUA), 300,
                ignored -> TeleportHologramDialog.create(hologram)));
        actions.add(DialogControl.actionButton(Component.text("Rename Hologram", NamedTextColor.YELLOW), 300,
                ignored -> RenameHologramDialog.create(hologram, hologram.getName(), null)));
        actions.add(ActionButton.builder(Component.text("Delete Hologram", NamedTextColor.RED))
                .action(DialogControl.openDirect(audience -> DeleteHologramDialog.create(hologram, audience))).width(300).build());

        actions.add(back);

        final var dialog = DialogControl.create(hologram.getName()).actions(actions).columns(1);
        if (lines.isEmpty()) dialog.body("No lines have been added yet");
        return dialog;
    }

    static DialogControl create(final Hologram hologram) {
        return EditHologramDialog.create(hologram, Audience.empty());
    }
}
