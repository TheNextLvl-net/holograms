package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class EditTextLineDialog {
    private EditTextLineDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final TextHologramLine line,
            final String initial,
            @Nullable final Component note
    ) {
        final var remove = DialogSupport.deleteLineButton(hologram, lineIndex).width(300);

        final var save = Button.callback((response, audience) -> {
            final var input = response.getText("text");
            final var text = input != null ? input.trim() : null;
            if (text == null || text.isBlank()) {
                DialogSupport.show(audience, ignored -> EditTextLineDialog.create(hologram, lineIndex, line, "", Component.text("Text cannot be empty", NamedTextColor.RED)));
                return;
            }

            line.setUnparsedText(DialogSupport.saveLineBreaks(text));
            DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
        }, Component.text("Save")).uses(1).width(300);
        final var visual = Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, current -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, current));
        }), Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE)).width(300);
        final var actionsButton = DialogSupport.clickActionsButton(hologram, line, DialogSupport.lineLabel(lineIndex, line), note,
                audience -> EditTextLineDialog.create(hologram, lineIndex, line, note));
        final var dialog = Dialog.multiAction()
                .title(DialogSupport.lineLabel(lineIndex, line))
                .addBody(Body.text(Component.text("Edit the text for this line")))
                .addInput(Input.text("text", Component.text("Line text"))
                        .initial(DialogSupport.loadLineBreaks(initial))
                        .maxLength(8192)
                        .multiline(TextDialogInput.MultilineOptions.create(null, 120)))
                .addButton(save)
                .addButton(visual)
                .addButton(actionsButton)
                .addButton(remove)
                .columns(1)
                .exitAction(BackButton.create(300, current -> EditHologramDialog.create(hologram, current)));
        if (note != null) dialog.addBody(Body.text(note));
        return dialog;
    }

    static Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final TextHologramLine line,
            @Nullable final Component note
    ) {
        return EditTextLineDialog.create(hologram, lineIndex, line, line.getUnparsedText().orElse(""), note);
    }
}
