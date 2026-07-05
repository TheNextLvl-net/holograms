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
import net.thenextlvl.hologram.line.HologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

@NullMarked
final class DeleteActionDialog {
    private DeleteActionDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, DialogLike> reopen
    ) {
        final var confirm = ActionButton.builder(Component.text("Delete", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    line.removeAction(actionName);
                    DialogSupport.show(audience, ignored -> ClickActionsDialog.create(hologram, line, header, note, reopen));
                })))
                .build();
        final var cancel = ActionButton.builder(Component.text("Cancel"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    final var current = line.getAction(actionName).orElse(null);
                    if (current == null) {
                        DialogSupport.show(audience, ignored -> ClickActionsDialog.create(hologram, line, header, note, reopen));
                        return;
                    }
                    DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, current, header, note, reopen));
                })))
                .build();
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Delete action " + actionName + "?"))
                        .body(List.of(DialogBody.plainMessage(Component.text("This cannot be undone"))))
                        .build())
                .type(DialogType.confirmation(confirm, cancel)));
    }
}
