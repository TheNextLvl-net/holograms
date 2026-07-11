package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;

@NullMarked
final class AddLineTypeDialog {
    private AddLineTypeDialog() {
    }

    static Dialog<?> create(final Hologram hologram) {
        final var actions = new ArrayList<Button<?>>();
        actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, ignored -> AddLineDialog.create(hologram, "", null));
        }), Component.text("Text", NamedTextColor.GREEN)).width(300));
        actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, current -> AddItemLineDialog.create(hologram, "", null, current));
        }), Component.text("Item", NamedTextColor.GREEN)).width(300));
        actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, ignored -> AddBlockLineDialog.create(hologram, "", null));
        }), Component.text("Block", NamedTextColor.GREEN)).width(300));
        actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, ignored -> AddEntityLineDialog.create(hologram, "", null));
        }), Component.text("Entity", NamedTextColor.GREEN)).width(300));
        actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
            final var lineIndex = hologram.getLines().toList().size();
            final var line = hologram.addPagedLine();
            DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
        }), Component.text("Paged", NamedTextColor.GREEN)).width(300));
        actions.add(BackButton.create(current -> EditHologramDialog.create(hologram, current)).width(300));

        final var dialog = Dialog.multiAction().title(Component.text("Add Line")).columns(1);
        actions.forEach(dialog::addButton);
        return dialog;
    }
}
