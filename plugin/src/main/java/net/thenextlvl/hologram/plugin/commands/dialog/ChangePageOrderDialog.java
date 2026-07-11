package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
final class ChangePageOrderDialog {
    private ChangePageOrderDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final Audience viewer
    ) {
        final var swap = Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, current -> SelectPageToSwapDialog.create(hologram, lineIndex, line, current));
        }), Component.text("Swap two pages", NamedTextColor.YELLOW)).width(300);
        final var move = Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, current -> SelectPageToMoveDialog.create(hologram, lineIndex, line, current));
        }), Component.text("Move page above another", NamedTextColor.YELLOW)).width(300);
        final var back = DialogSupport.editPageBackButton(hologram, lineIndex, line);

        final var dialog = Dialog.multiAction().title(Component.text("Change order"));
        List.of(Body.text(Component.text("Choose whether to swap two pages or move one page above another"))).forEach(dialog::addBody);
        List.of(swap, move).forEach(dialog::addButton);
        dialog.exitAction(back);
        dialog.columns(1);
        return dialog;
    }
}
