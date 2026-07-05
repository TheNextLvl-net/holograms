package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.EntityHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

@NullMarked
final class EditEntityLineVisualsDialog {
    private EditEntityLineVisualsDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final EntityHologramLine line,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> EditEntityLineDialog.create(hologram, lineIndex, line, note));
                })))
                .build();

        final var actions = new ArrayList<ActionButton>();
        actions.add(DialogSupport.visualGlowButton(hologram, line, audience -> EditEntityLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        actions.add(DialogSupport.visualGlowColorButton(hologram, line, audience -> EditEntityLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        actions.add(DialogSupport.visualBillboardButton(hologram, line, audience -> EditEntityLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        actions.add(DialogSupport.visualOffsetButton(hologram, line, audience -> EditEntityLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        actions.add(DialogSupport.visualScaleButton(line.getScale(), 0.1d, 100.0d, (audience, value) -> {
            line.setScale(value);
            DialogSupport.show(audience, current -> EditEntityLineVisualsDialog.create(hologram, lineIndex, line, note, current));
        }, audience -> EditEntityLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        return VisualOptionsDialog.create("Visual Options", DialogSupport.lineLabel(lineIndex, line), note, actions, back);
    }
}
