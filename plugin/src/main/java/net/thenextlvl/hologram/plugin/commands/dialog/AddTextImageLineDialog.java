package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class AddTextImageLineDialog {
    private AddTextImageLineDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final String textInitial,
            final String sourceInitial,
            final String sizeInitial,
            @Nullable final Component note
    ) {
        final var back = BackButton.create(ignored -> AddLineDialog.create(hologram, textInitial, note)).width(150);

        final var add = Button.callback((response, audience) -> {
            final var input = response.getText("source");
            final var source = input != null ? input.trim() : null;
            if (source == null || source.isBlank()) {
                DialogSupport.show(audience, ignored -> AddTextImageLineDialog.create(hologram, textInitial, "", sizeInitial, Component.text("Image source cannot be empty", NamedTextColor.RED)));
                return;
            }

            final var size = response.getText("size");
            final var height = DialogSupport.parseImageHeight(size);
            if (height.error() != null) {
                DialogSupport.show(audience, ignored -> AddTextImageLineDialog.create(hologram, textInitial, source, size != null ? size : sizeInitial,
                        Component.text(height.error(), NamedTextColor.RED)));
                return;
            }

            final var image = DialogSupport.parseImageSource(source);
            if (image.error() != null) {
                DialogSupport.show(audience, ignored -> AddTextImageLineDialog.create(hologram, textInitial, source, size != null ? size : sizeInitial,
                        Component.text(image.error(), NamedTextColor.RED)));
                return;
            }

            hologram.addTextLine().setUnparsedText(DialogSupport.imageTag(source, height.value()));
            DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
        }, Component.text("Add")).uses(1);

        final var dialog = Dialog.multiAction()
                .title(Component.text("Add Image Line"))
                .addBody(Body.text(Component.text("Enter the URL or file path for the image")));
        if (note != null) dialog.addBody(Body.text(note));
        return dialog.addInput(Input.text("source", Component.text("Image source"))
                        .initial(sourceInitial)
                        .maxLength(8192))
                .addInput(Input.text("size", Component.text("Image size"))
                        .initial(sizeInitial))
                .addButton(add)
                .exitAction(back);
    }
}
