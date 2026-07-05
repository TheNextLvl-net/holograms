package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.ItemHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

@NullMarked
final class EditItemLineDialog {
    private EditItemLineDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final ItemHologramLine line,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var actions = new ArrayList<ActionButton>();
        final var setHeld = DialogSupport.heldItemButton(viewer, "Set to Held Item", (audience, item) -> {
            line.setItemStack(item);
            DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
        });
        if (setHeld != null) actions.add(setHeld);
        final var visual = ActionButton.builder(Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditItemLineVisualsDialog.create(hologram, lineIndex, line, note, current));
                }))).build();
        final var playerHead = ActionButton.builder(Component.text(
                        "Player head: " + (line.isPlayerHead() ? "On" : "Off"),
                        line.isPlayerHead() ? NamedTextColor.GREEN : NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    line.setPlayerHead(!line.isPlayerHead());
                    DialogSupport.show(audience, current -> EditItemLineDialog.create(hologram, lineIndex, line, note, current));
                }))).build();
        final var actionsButton = DialogSupport.clickActionsButton(hologram, line, DialogSupport.lineLabel(lineIndex, line), note,
                audience -> EditItemLineDialog.create(hologram, lineIndex, line, note, viewer));
        final var remove = DialogSupport.deleteLineButton(hologram, lineIndex, false);
        actions.add(visual);
        actions.add(playerHead);
        actions.add(actionsButton);
        actions.add(remove);
        final var back = DialogSupport.editHologramBackButton(hologram);
        return ItemSearchDialog.create(DialogSupport.lineLabel(lineIndex, line), line.getItemStack().getType().key().asString(), note,
                actions, back, (audience, item) -> {
                    line.setItemStack(item);
                    DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                });
    }
}
