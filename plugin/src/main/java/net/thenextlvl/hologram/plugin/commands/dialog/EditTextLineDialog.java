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
import net.thenextlvl.hologram.line.TextHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
final class EditTextLineDialog {
    private EditTextLineDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final TextHologramLine line,
            final String initial,
            @Nullable final Component note
    ) {
        final var remove = ActionButton.builder(Component.text("Delete this line", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(DeleteLineDialog.create(hologram, lineIndex, audience));
                }))).width(300).build();

        final var save = ActionButton.builder(Component.text("Save"))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("text");
                    final var text = input != null ? input.trim() : null;
                    if (text == null || text.isBlank()) {
                        DialogSupport.show(audience, ignored -> EditTextLineDialog.create(hologram, lineIndex, line, "", Component.text("Text cannot be empty", NamedTextColor.RED)));
                        return;
                    }

                    line.setUnparsedText(DialogSupport.saveLineBreaks(text));
                    DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                }, ClickCallback.Options.builder().uses(1).build()))
                .width(300).build();
        final var visual = ActionButton.builder(Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, current));
                }))).width(300).build();
        final var actionsButton = DialogSupport.clickActionsButton(hologram, line, DialogSupport.lineLabel(lineIndex, line), note,
                audience -> EditTextLineDialog.create(hologram, lineIndex, line, note));

        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                })))
                .width(300)
                .build();

        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Edit the text for this line")));
        if (note != null) body.add(DialogBody.plainMessage(note));

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(DialogSupport.lineLabel(lineIndex, line))
                        .body(body)
                        .inputs(List.of(DialogInput.text("text", Component.text("Line text"))
                                .initial(DialogSupport.loadLineBreaks(initial))
                                .maxLength(8192)
                                .multiline(TextDialogInput.MultilineOptions.create(null, 120))
                                .build()))
                        .build())
                .type(DialogType.multiAction(List.of(save, visual, actionsButton, remove)).columns(1).exitAction(back).build()));
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final TextHologramLine line,
            @Nullable final Component note
    ) {
        return EditTextLineDialog.create(hologram, lineIndex, line, line.getUnparsedText().orElse(""), note);
    }
}
