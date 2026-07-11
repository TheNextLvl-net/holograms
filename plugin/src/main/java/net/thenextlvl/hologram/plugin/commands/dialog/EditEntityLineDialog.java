package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.EntityHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class EditEntityLineDialog {
    private EditEntityLineDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final EntityHologramLine line,
            @Nullable final Component note
    ) {
        final var remove = DialogSupport.deleteLineButton(hologram, lineIndex, false);
        final var visual = Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, current -> EditEntityLineVisualsDialog.create(hologram, lineIndex, line, note, current));
        }), Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE));
        final var actionsButton = DialogSupport.clickActionsButton(hologram, line, DialogSupport.lineLabel(lineIndex, line), note,
                audience -> EditEntityLineDialog.create(hologram, lineIndex, line, note));
        final var back = DialogSupport.editHologramBackButton(hologram);
        return EntitySearchDialog.create(DialogSupport.lineLabel(lineIndex, line), line.getEntityType().key().asString(), note, back,
                (audience, entityType) -> {
                    line.setEntityType(entityType);
                    DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                }, visual, actionsButton, remove);
    }
}
