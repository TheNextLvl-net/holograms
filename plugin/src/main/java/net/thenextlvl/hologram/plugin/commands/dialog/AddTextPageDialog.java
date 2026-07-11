package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.MultiActionDialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class AddTextPageDialog {
    private AddTextPageDialog() {
    }

    static MultiActionDialog create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final String initial,
            @Nullable final Component note
    ) {
        final var dialog = Dialog.multiAction()
                .title(Component.text("Add Text Page"));
        if (note != null) dialog.addBody(Body.text(note));
        return dialog
                .addInput(Input.text("text", Component.text("Page text"))
                        .initial(DialogSupport.loadLineBreaks(initial)))
                .addButton(Button.callback((response, audience) -> {
                    final var input = response.getText("text");
                    final var text = input != null ? input.trim() : null;
                    if (text == null || text.isBlank()) {
                        DialogSupport.show(audience, ignored -> AddTextPageDialog.create(hologram, lineIndex, line, "", Component.text("Text cannot be empty", NamedTextColor.RED)));
                        return;
                    }
                    line.addTextPage().setUnparsedText(DialogSupport.saveLineBreaks(text));
                    DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
                }, Component.text("Add")).uses(1))
                .addButton(DialogButton.create(ignored -> {
                    return AddTextImagePageDialog.create(hologram, lineIndex, line, initial, "", "8", note);
                }, Component.text("Image", NamedTextColor.AQUA)))
                .exitAction(DialogSupport.editPagedBackButton(hologram, lineIndex, line));
    }
}
