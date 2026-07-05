package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;

@NullMarked
final class AddLineTypeDialog {
    private AddLineTypeDialog() {
    }

    static DialogLike create(final Hologram hologram) {
        final var actions = new ArrayList<ActionButton>();
        actions.add(ActionButton.builder(Component.text("Text", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> AddLineDialog.create(hologram, "", null));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Item", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> AddItemLineDialog.create(hologram, "", null, current));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Block", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> AddBlockLineDialog.create(hologram, "", null));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Entity", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> AddEntityLineDialog.create(hologram, "", null));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Paged", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    final var lineIndex = hologram.getLines().toList().size();
                    final var line = hologram.addPagedLine();
                    DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                }))).width(300).build());

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Add Line")).build())
                .type(DialogType.multiAction(actions).columns(1).build()));
    }
}
