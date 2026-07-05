package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
final class DeletePageDialog {
    private DeletePageDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final int pageIndex
    ) {
        final var confirm = ActionButton.builder(Component.text("Delete this page", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    line.removePage(pageIndex);
                    DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
                }))).build();

        final var cancel = ActionButton.builder(Component.text("Cancel"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditPageDialog.create(hologram, lineIndex, line, pageIndex, current));
                }))).build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Delete page " + (pageIndex + 1) + "?"))
                        .body(List.of(DialogBody.plainMessage(Component.text("This cannot be undone"))))
                        .build())
                .type(DialogType.confirmation(confirm, cancel)));
    }
}
