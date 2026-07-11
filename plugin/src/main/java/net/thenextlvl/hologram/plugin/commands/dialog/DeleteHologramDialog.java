package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramProvider;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
final class DeleteHologramDialog {
    private DeleteHologramDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(final Hologram hologram, final Audience viewer) {
        final var confirm = Button.clickEvent(ClickEvent.callback(audience -> {
            HologramProvider.instance().deleteHologram(hologram);
            DialogSupport.show(audience, ignored -> OverviewDialog.create());
        }), Component.text("Delete Hologram", NamedTextColor.RED));

        final var cancel = Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
        }), Component.text("Cancel"));

        final var dialog = Dialog.confirmation(cancel, confirm).title(Component.text("Delete " + hologram.getName() + "?"));
        List.of(Body.text(Component.text("This cannot be undone"))).forEach(dialog::addBody);
        return dialog;
    }
}
