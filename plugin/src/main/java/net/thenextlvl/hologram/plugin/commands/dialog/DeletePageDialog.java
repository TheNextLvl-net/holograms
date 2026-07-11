package net.thenextlvl.hologram.plugin.commands.dialog;

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
final class DeletePageDialog {
    private DeletePageDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final int pageIndex
    ) {
        final var confirm = Button.clickEvent(ClickEvent.callback(audience -> {
            line.removePage(pageIndex);
            DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
        }), Component.text("Delete this page", NamedTextColor.RED));

        final var cancel = Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, current -> EditPageDialog.create(hologram, lineIndex, line, pageIndex, current));
        }), Component.text("Cancel"));

        final var dialog = Dialog.confirmation(cancel, confirm).title(Component.text("Delete page " + (pageIndex + 1) + "?"));
        List.of(Body.text(Component.text("This cannot be undone"))).forEach(dialog::addBody);
        return dialog;
    }
}
