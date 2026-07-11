package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
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

import java.util.ArrayList;
import java.util.List;

@NullMarked
final class AddTextImagePageDialog {
    private AddTextImagePageDialog() {
    }

    static MultiActionDialog create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final String textInitial,
            final String sourceInitial,
            final String sizeInitial,
            @Nullable final Component note
    ) {
        final var back = BackButton.create(ignored -> AddTextPageDialog.create(hologram, lineIndex, line, textInitial, note)).width(150);

        final var body = new ArrayList<DialogBody>();
        body.add(Body.text(Component.text("Enter the URL or file path for the image")));
        if (note != null) body.add(Body.text(note));

        final var add = Button.callback((response, audience) -> {
            final var input = response.getText("source");
            final var source = input != null ? input.trim() : null;
            if (source == null || source.isBlank()) {
                DialogSupport.show(audience, ignored -> AddTextImagePageDialog.create(hologram, lineIndex, line, textInitial, "", sizeInitial, Component.text("Image source cannot be empty", NamedTextColor.RED)));
                return;
            }

            final var size = response.getText("size");
            final var height = DialogSupport.parseImageHeight(size);
            if (height.error() != null) {
                DialogSupport.show(audience, ignored -> AddTextImagePageDialog.create(hologram, lineIndex, line, textInitial, source, size != null ? size : sizeInitial,
                        Component.text(height.error(), NamedTextColor.RED)));
                return;
            }

            final var image = DialogSupport.parseImageSource(source);
            if (image.error() != null) {
                DialogSupport.show(audience, ignored -> AddTextImagePageDialog.create(hologram, lineIndex, line, textInitial, source, size != null ? size : sizeInitial,
                        Component.text(image.error(), NamedTextColor.RED)));
                return;
            }

            line.addTextPage().setUnparsedText(DialogSupport.imageTag(source, height.value()));
            DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
        }, Component.text("Add")).uses(1);

        final var dialog = Dialog.multiAction().title(Component.text("Add Image Page"));
        body.forEach(dialog::addBody);
        List.of(
                Input.text("source", Component.text("Image source"))
                        .initial(sourceInitial)
                        .build(),
                Input.text("size", Component.text("Image size"))
                        .initial(sizeInitial)
                        .build()
        ).forEach(dialog::addInput);
        List.of(add).forEach(dialog::addButton);
        dialog.exitAction(back);
        return dialog;
    }
}
