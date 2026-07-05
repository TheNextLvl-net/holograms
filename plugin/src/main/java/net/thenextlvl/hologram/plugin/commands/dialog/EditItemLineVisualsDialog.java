package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.ItemHologramLine;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

@NullMarked
final class EditItemLineVisualsDialog {
    private EditItemLineVisualsDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final ItemHologramLine line,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> EditItemLineDialog.create(hologram, lineIndex, line, note, viewer));
                })))
                .build();

        final var actions = new ArrayList<ActionButton>();
        final var held = DialogSupport.heldItemButton(viewer, "Set to Held Item", (audience, item) -> {
            line.setItemStack(item);
            DialogSupport.show(audience, current -> EditItemLineVisualsDialog.create(hologram, lineIndex, line, note, current));
        });
        if (held != null) actions.add(held);
        actions.add(DialogSupport.toggleButton("Player Head", line.isPlayerHead(), (audience, value) -> {
            line.setPlayerHead(value);
            DialogSupport.show(audience, current -> EditItemLineVisualsDialog.create(hologram, lineIndex, line, note, current));
        }));
        actions.add(DialogSupport.enumButton("Item Display", line.getItemDisplayTransform(), ItemDisplayTransform.class, line::setItemDisplayTransform,
                audience -> EditItemLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        actions.add(DialogSupport.visualGlowButton(hologram, line, audience -> EditItemLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        actions.add(DialogSupport.visualGlowColorButton(hologram, line, audience -> EditItemLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        actions.add(DialogSupport.visualBillboardButton(hologram, line, audience -> EditItemLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        actions.add(DialogSupport.visualOffsetButton(hologram, line, audience -> EditItemLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        actions.add(DialogSupport.visualDisplayScaleButton(line, audience -> EditItemLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        actions.add(DialogSupport.visualBrightnessButton(hologram, line, audience -> EditItemLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        return VisualOptionsDialog.create("Visual Options", DialogSupport.lineLabel(lineIndex, line), note, actions, back);
    }
}
