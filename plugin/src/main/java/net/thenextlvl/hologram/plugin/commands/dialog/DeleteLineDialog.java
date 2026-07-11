package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
final class DeleteLineDialog {
    private DeleteLineDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(final Hologram hologram, final int lineIndex, final Audience viewer) {
        final var confirm = Button.clickEvent(ClickEvent.callback(audience -> {
            hologram.removeLine(lineIndex);
            DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
        }), Component.text("Delete this line", NamedTextColor.RED));

        final var cancel = Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, current -> EditLineDialog.create(hologram, lineIndex, current));
        }), Component.text("Cancel"));

        final var dialog = Dialog.confirmation(cancel, confirm).title(Component.text("Delete line " + (lineIndex + 1) + "?"));
        List.of(Body.text(Component.text("This cannot be undone"))).forEach(dialog::addBody);
        return dialog;
    }
}
