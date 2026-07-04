package net.thenextlvl.hologram.commands.dialog;

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
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
final class ChangeLineOrderDialog {
    private ChangeLineOrderDialog() {
    }

    static DialogLike create(final Hologram hologram, final Audience viewer) {
        final var swap = ActionButton.builder(Component.text("Swap two lines", NamedTextColor.YELLOW))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> SelectLineToSwapDialog.create(hologram, current));
                }))).width(300).build();
        final var move = ActionButton.builder(Component.text("Move line above another", NamedTextColor.YELLOW))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> SelectLineToMoveDialog.create(hologram, current));
                }))).width(300).build();
        final var back = DialogSupport.editHologramBackButton(hologram);

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Change order"))
                        .body(List.of(DialogBody.plainMessage(Component.text("Choose whether to swap two lines or move one line above another"))))
                        .build())
                .type(DialogType.multiAction(List.of(swap, move)).columns(1).exitAction(back).build()));
    }
}
