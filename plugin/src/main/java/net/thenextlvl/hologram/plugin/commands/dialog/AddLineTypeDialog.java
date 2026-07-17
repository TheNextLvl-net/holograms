package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.DialogBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class AddLineTypeDialog {
    private AddLineTypeDialog() {
    }

    static Dialog<?> create(final Hologram hologram) {
        return Dialog.multiAction()
                .closeAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)
                .title(Component.text("Add Line"))
                .columns(1)
                .addButton(Button.clickEvent(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> AddLineDialog.create(hologram, "", null));
                }), Component.text("Text", NamedTextColor.GREEN)).width(300))
                .addButton(Button.clickEvent(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> AddItemLineDialog.create(hologram, "", null, current));
                }), Component.text("Item", NamedTextColor.GREEN)).width(300))
                .addButton(Button.clickEvent(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> AddBlockLineDialog.create(hologram, "", null));
                }), Component.text("Block", NamedTextColor.GREEN)).width(300))
                .addButton(Button.clickEvent(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> AddEntityLineDialog.create(hologram, "", null));
                }), Component.text("Entity", NamedTextColor.GREEN)).width(300))
                .addButton(Button.clickEvent(ClickEvent.callback(audience -> {
                    final var lineIndex = hologram.getLines().toList().size();
                    final var line = hologram.addPagedLine();
                    DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
                }), Component.text("Paged", NamedTextColor.GREEN)).width(300))
                .addButton(BackButton.create(current -> {
                    return EditHologramDialog.create(hologram, current);
                }).width(300));
    }
}
