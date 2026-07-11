package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
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
final class AddTextPageDialog {
    private AddTextPageDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final String initial,
            @Nullable final Component note
    ) {
        final var add = Button.callback((response, audience) -> {
            final var input = response.getText("text");
            final var text = input != null ? input.trim() : null;
            if (text == null || text.isBlank()) {
                DialogSupport.show(audience, ignored -> AddTextPageDialog.create(hologram, lineIndex, line, "", Component.text("Text cannot be empty", NamedTextColor.RED)));
                return;
            }
            line.addTextPage().setUnparsedText(DialogSupport.saveLineBreaks(text));
            DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
        }, Component.text("Add")).uses(1);
        final var image = Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, ignored -> AddTextImagePageDialog.create(hologram, lineIndex, line, initial, "", "8", note));
        }), Component.text("Image", NamedTextColor.AQUA));
        final var body = new ArrayList<DialogBody>();
        if (note != null) body.add(Body.text(note));
        final var dialog = Dialog.multiAction().title(Component.text("Add Text Page"));
        body.forEach(dialog::addBody);
        List.of(Input.text("text", Component.text("Page text"))
                .initial(DialogSupport.loadLineBreaks(initial))
                .maxLength(8192)
                .multiline(TextDialogInput.MultilineOptions.create(null, 120))
                .build()).forEach(dialog::addInput);
        List.of(add, image).forEach(dialog::addButton);
        dialog.exitAction(DialogSupport.editPagedBackButton(hologram, lineIndex, line));
        return dialog;
    }
}
