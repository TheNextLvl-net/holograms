package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditLineDialog {
    private EditLineDialog() {
    }

    static Dialog<?> create(final Hologram hologram, final int lineIndex, final Audience viewer) {
        final var line = hologram.getLine(lineIndex).orElse(null);
        if (line == null) return EditHologramDialog.create(hologram, viewer);

        final var back = BackButton.create(current -> EditHologramDialog.create(hologram, current)).width(300);

        return switch (line) {
            case final TextHologramLine textLine -> EditTextLineDialog.create(hologram, lineIndex, textLine, null);
            case final BlockHologramLine blockLine -> EditBlockLineDialog.create(hologram, lineIndex, blockLine, null);
            case final ItemHologramLine itemLine ->
                    EditItemLineDialog.create(hologram, lineIndex, itemLine, null, viewer);
            case final EntityHologramLine entityLine ->
                    EditEntityLineDialog.create(hologram, lineIndex, entityLine, null);
            case final PagedHologramLine pagedLine ->
                    EditPagedLineDialog.create(hologram, lineIndex, pagedLine, viewer);
            default -> Dialog.multiAction()
                    .title(DialogSupport.lineLabel(lineIndex, line))
                    .addBody(Body.text(Component.text("This line type cannot be edited in dialogs yet")))
                    .addButton(DialogSupport.deleteLineButton(hologram, lineIndex).width(300))
                    .addButton(back)
                    .columns(1);
        };

    }
}
