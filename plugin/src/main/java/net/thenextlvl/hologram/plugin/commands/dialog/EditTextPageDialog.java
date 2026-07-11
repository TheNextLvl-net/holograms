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
import net.thenextlvl.hologram.line.TextHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
        final var save = Button.callback((response, audience) -> {
            final var input = response.getText("text");
            final var text = input != null ? input.trim() : null;
            if (text == null || text.isBlank()) {
                DialogSupport.show(audience, ignored -> EditTextPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, page,
                        Component.text("Text cannot be empty", NamedTextColor.RED)));
                return;
            }
            page.setUnparsedText(DialogSupport.saveLineBreaks(text));
            DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, pagedLine, current));
        }, Component.text("Save")).uses(1).width(300);
        final var visual = Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, current -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }), Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE)).width(300);
        final var actionsButton = DialogSupport.clickActionsButton(hologram, page, Component.text("Page " + (pageIndex + 1)), note,
                audience -> EditTextPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note));
        final var body = new ArrayList<DialogBody>();
        if (note != null) body.add(Body.text(note));
        final var dialog = Dialog.multiAction().title(Component.text("Page " + (pageIndex + 1)));
        body.forEach(dialog::addBody);
        List.of(Input.text("text", Component.text("Page text"))
                .initial(DialogSupport.loadLineBreaks(page.getUnparsedText().orElse("")))
                .maxLength(8192)
                .multiline(TextDialogInput.MultilineOptions.create(null, 120))
                .build()).forEach(dialog::addInput);
        List.of(save, visual, actionsButton, DialogSupport.deletePageButton(hologram, lineIndex, pagedLine, pageIndex).width(300)).forEach(dialog::addButton);
        dialog.exitAction(DialogSupport.editPageBackButton(hologram, lineIndex, pagedLine));
        dialog.columns(1);
        return dialog;
    }
}
