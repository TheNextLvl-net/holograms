package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final TextHologramLine page,
            @Nullable final Component note
    ) {
        final var save = ActionButton.builder(Component.text("Save")).width(300)
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("text");
                    final var text = input != null ? input.trim() : null;
                    if (text == null || text.isBlank()) {
                        DialogSupport.show(audience, ignored -> EditTextPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, page,
                                Component.text("Text cannot be empty", NamedTextColor.RED)));
                        return;
                    }
                    page.setUnparsedText(DialogSupport.saveLineBreaks(text));
                    DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, pagedLine, current));
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var visual = ActionButton.builder(Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, current));
                }))).width(300).build();
        final var actionsButton = DialogSupport.clickActionsButton(hologram, page, Component.text("Page " + (pageIndex + 1)), note,
                audience -> EditTextPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note));
        final var body = new ArrayList<DialogBody>();
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Page " + (pageIndex + 1)))
                        .body(body)
                        .inputs(List.of(DialogInput.text("text", Component.text("Page text"))
                                .initial(DialogSupport.loadLineBreaks(page.getUnparsedText().orElse("")))
                                .maxLength(8192)
                                .multiline(TextDialogInput.MultilineOptions.create(null, 120))
                                .build()))
                        .build())
                .type(DialogType.multiAction(List.of(save, visual, actionsButton, DialogSupport.deletePageButton(hologram, lineIndex, pagedLine, pageIndex)))
                        .columns(1)
                        .exitAction(DialogSupport.editPageBackButton(hologram, lineIndex, pagedLine))
                        .build()));
    }
}
