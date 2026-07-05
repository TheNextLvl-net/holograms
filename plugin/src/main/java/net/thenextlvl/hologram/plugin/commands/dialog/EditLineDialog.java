package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
final class EditLineDialog {
    private EditLineDialog() {
    }

    static DialogLike create(final Hologram hologram, final int lineIndex, final Audience viewer) {
        final var line = hologram.getLine(lineIndex).orElse(null);
        if (line == null) return EditHologramDialog.create(hologram, viewer);

        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                })))
                .width(300)
                .build();

        final var remove = ActionButton.builder(Component.text("Delete this line", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(DeleteLineDialog.create(hologram, lineIndex, audience));
                }))).width(300).build();

        return switch (line) {
            case final TextHologramLine textLine -> EditTextLineDialog.create(hologram, lineIndex, textLine, null);
            case final BlockHologramLine blockLine ->
                    EditBlockLineDialog.create(hologram, lineIndex, blockLine, null);
            case final ItemHologramLine itemLine ->
                    EditItemLineDialog.create(hologram, lineIndex, itemLine, null, viewer);
            case final EntityHologramLine entityLine ->
                    EditEntityLineDialog.create(hologram, lineIndex, entityLine, null);
            case final PagedHologramLine pagedLine ->
                    EditPagedLineDialog.create(hologram, lineIndex, pagedLine, viewer);
            default -> Dialog.create(builder -> builder.empty()
                    .base(DialogBase.builder(DialogSupport.lineLabel(lineIndex, line))
                            .body(List.of(DialogBody.plainMessage(Component.text("This line type cannot be edited in dialogs yet"))))
                            .build())
                    .type(DialogType.multiAction(List.of(remove, back)).columns(1).build()));
        };

    }
}
