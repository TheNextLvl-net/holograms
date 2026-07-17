package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.DialogBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.button.ClickEventButton;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramProvider;
import org.jspecify.annotations.NullMarked;

import java.util.Comparator;

@NullMarked
final class OverviewDialog {
    private OverviewDialog() {
    }

    public static net.thenextlvl.dialogs.Dialog<?> create() {
        final var dialog = Dialog.multiAction()
                .closeAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)
                .title(Component.text("Manage your Holograms"));
        final var holograms = HologramProvider.instance().getHolograms()
                .sorted(Comparator.comparing(Hologram::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        holograms.stream().map(OverviewDialog::editHologram).forEach(dialog::addButton);
        if (holograms.isEmpty()) dialog.addBody(Body.text(Component.text("No holograms have been created yet")));

        dialog.addButton(newHologramButton());
        return dialog;
    }

    private static ClickEventButton newHologramButton() {
        return Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, ignored -> CreateHologramDialog.create("", null));
        }), Component.text("New Hologram", NamedTextColor.GREEN));
    }

    private static ClickEventButton editHologram(final Hologram hologram) {
        return Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, viewer -> EditHologramDialog.create(hologram, viewer));
        }), Component.text(hologram.getName()));
    }
}
