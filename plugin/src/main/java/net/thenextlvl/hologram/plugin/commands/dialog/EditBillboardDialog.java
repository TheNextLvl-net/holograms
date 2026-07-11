package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.line.StaticHologramLine;
import org.bukkit.entity.Display;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.function.Function;

@NullMarked
final class EditBillboardDialog {
    private EditBillboardDialog() {
    }

    static Dialog<?> create(
            final StaticHologramLine line,
            final Function<Audience, Dialog<?>> reopen
    ) {
        final var actions = new ArrayList<Button<?>>();
        for (final var billboard : Display.Billboard.values()) {
            actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
                line.setBillboard(billboard);
                DialogSupport.show(audience, reopen);
            }), Component.text(DialogSupport.friendlyName(billboard.name()))));
        }
        final var dialog = Dialog.multiAction()
                .title(Component.text("Billboard"))
                .addBody(Body.text(Component.text("Choose the billboard mode")))
                .exitAction(BackButton.create(reopen));
        actions.forEach(dialog::addButton);
        return dialog;
    }
}
