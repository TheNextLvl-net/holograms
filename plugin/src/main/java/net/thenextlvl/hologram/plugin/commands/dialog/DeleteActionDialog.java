package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
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

    static net.thenextlvl.dialogs.Dialog<?> create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, net.thenextlvl.dialogs.Dialog<?>> reopen
    ) {
        final var confirm = Button.clickEvent(ClickEvent.callback(audience -> {
            line.removeAction(actionName);
            DialogSupport.show(audience, ignored -> ClickActionsDialog.create(hologram, line, header, note, reopen));
        }), Component.text("Delete", NamedTextColor.RED));
        final var cancel = Button.clickEvent(ClickEvent.callback(audience -> {
            final var current = line.getAction(actionName).orElse(null);
            if (current == null) {
                DialogSupport.show(audience, ignored -> ClickActionsDialog.create(hologram, line, header, note, reopen));
                return;
            }
            DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, current, header, note, reopen));
        }), Component.text("Cancel"));
        final var dialog = Dialog.confirmation(cancel, confirm).title(Component.text("Delete action " + actionName + "?"));
        List.of(Body.text(Component.text("This cannot be undone"))).forEach(dialog::addBody);
        return dialog;
    }
}
