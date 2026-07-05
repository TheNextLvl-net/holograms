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
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
final class ChangePageOrderDialog {
    private ChangePageOrderDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final Audience viewer
    ) {
        final var swap = ActionButton.builder(Component.text("Swap two pages", NamedTextColor.YELLOW))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> SelectPageToSwapDialog.create(hologram, lineIndex, line, current));
                }))).width(300).build();
        final var move = ActionButton.builder(Component.text("Move page above another", NamedTextColor.YELLOW))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> SelectPageToMoveDialog.create(hologram, lineIndex, line, current));
                }))).width(300).build();
        final var back = DialogSupport.editPageBackButton(hologram, lineIndex, line);

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Change order"))
                        .body(List.of(DialogBody.plainMessage(Component.text("Choose whether to swap two pages or move one page above another"))))
                        .build())
                .type(DialogType.multiAction(List.of(swap, move)).columns(1).exitAction(back).build()));
    }
}
