package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class AddLineDialog {
    private AddLineDialog() {
    }

    static Dialog<?> create(final Hologram hologram, final String initial, @Nullable final Component note) {
        final var back = BackButton.create(ignored -> AddLineTypeDialog.create(hologram));

        final var dialog = Dialog.multiAction()
                .title(Component.text("Add Line"))
                .addBody(Body.text(Component.text("Enter the text for the new line")));
        if (note != null) dialog.addBody(Body.text(note));

        final var add = Button.callback((response, audience) -> {
            final var input = response.getText("text");
            final var text = input != null ? input.trim() : null;
            if (text == null || text.isBlank()) {
                DialogSupport.show(audience, ignored -> AddLineDialog.create(hologram, "", Component.text("Text cannot be empty", NamedTextColor.RED)));
                return;
            }

            hologram.addTextLine().setUnparsedText(DialogSupport.saveLineBreaks(text));
            DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
        }, Component.text("Add")).uses(1);
        final var image = Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, ignored -> AddTextImageLineDialog.create(hologram, initial, "", "8", note));
        }), Component.text("Image", NamedTextColor.AQUA));

        return dialog
                .addButton(add)
                .addButton(image)
                .addInput(Input.multiLineText("text", Component.text("Line text"))
                        .initial(DialogSupport.loadLineBreaks(initial))
                        .width(300)
                        .inputHeight(150))
                .exitAction(back);
    }
}
