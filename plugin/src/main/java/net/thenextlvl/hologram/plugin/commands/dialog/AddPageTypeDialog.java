package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class AddPageTypeDialog {
    private AddPageTypeDialog() {
    }

    static Dialog<?> create(final Hologram hologram, final int lineIndex, final PagedHologramLine line) {
        return Dialog.multiAction()
                .title(Component.text("Add Page"))
                .addButton(Button.clickEvent(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> AddTextPageDialog.create(hologram, lineIndex, line, "", null));
                }), Component.text("Text", NamedTextColor.GREEN)).width(300))
                .addButton(Button.clickEvent(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> AddItemPageDialog.create(hologram, lineIndex, line, "", null, current));
                }), Component.text("Item", NamedTextColor.GREEN)).width(300))
                .addButton(Button.clickEvent(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> AddBlockPageDialog.create(hologram, lineIndex, line, "", null));
                }), Component.text("Block", NamedTextColor.GREEN)).width(300))
                .addButton(Button.clickEvent(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> AddEntityPageDialog.create(hologram, lineIndex, line, "", null));
                }), Component.text("Entity", NamedTextColor.GREEN)).width(300))
                .addButton(Button.clickEvent(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
                }), Component.text("Back", NamedTextColor.GREEN)).width(300))
                .columns(1);
    }
}
