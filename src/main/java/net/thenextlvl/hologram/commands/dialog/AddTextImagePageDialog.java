package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final String textInitial,
            final String sourceInitial,
            final String sizeInitial,
            @Nullable final Component note
    ) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> AddTextPageDialog.create(hologram, lineIndex, line, textInitial, note));
                })))
                .width(150)
                .build();

        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter the URL or file path for the image")));
        if (note != null) body.add(DialogBody.plainMessage(note));

        final var add = ActionButton.builder(Component.text("Add"))
                .action(DialogAction.customClick((response, audience) -> {
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
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Add Image Page"))
                        .body(body)
                        .inputs(List.of(
                                DialogInput.text("source", Component.text("Image source"))
                                        .initial(sourceInitial)
                                        .maxLength(8192)
                                        .build(),
                                DialogInput.text("size", Component.text("Image size"))
                                        .initial(sizeInitial)
                                        .build()
                        )).build())
                .type(DialogType.multiAction(List.of(add)).exitAction(back).build()));
    }
}
