package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class EditTextPageDialog {
    private EditTextPageDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final TextHologramLine page,
            @Nullable final Component note
    ) {
        final var actionsButton = DialogSupport.clickActionsButton(hologram, page, Component.text("Page " + (pageIndex + 1)), note,
                audience -> EditTextPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note));
        final var dialog = Dialog.multiAction().title(Component.text("Page " + (pageIndex + 1)));
        if (note != null) dialog.addBody(Body.text(note));
        return dialog
                .addInput(Input.multiLineText("text", Component.text("Page text"))
                        .initial(DialogSupport.loadLineBreaks(page.getUnparsedText().orElse("")))
                        .inputHeight(10))
                .addButton(Button.callback((response, audience) -> {
                    final var input = response.getText("text");
                    final var text = input != null ? input.trim() : null;
                    if (text == null || text.isBlank()) {
                        DialogSupport.show(audience, ignored -> EditTextPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, page,
                                Component.text("Text cannot be empty", NamedTextColor.RED)));
                        return;
                    }
                    page.setUnparsedText(DialogSupport.saveLineBreaks(text));
                    DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, pagedLine, current));
                }, Component.text("Save")).uses(1).width(300))
                .addButton(DialogButton.create(current -> {
                    return EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, current);
                }, Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE)).width(300))
                .addButton(actionsButton)
                .addButton(DialogSupport.deletePageButton(hologram, lineIndex, pagedLine, pageIndex).width(300))
                .exitAction(DialogSupport.editPageBackButton(hologram, lineIndex, pagedLine))
                .columns(1);
    }
}
