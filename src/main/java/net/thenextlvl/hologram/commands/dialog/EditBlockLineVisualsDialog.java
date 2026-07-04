package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.BlockHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

@NullMarked
final class EditBlockLineVisualsDialog {
    private EditBlockLineVisualsDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final BlockHologramLine line,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> EditBlockLineDialog.create(hologram, lineIndex, line, note));
                })))
                .build();

        final var actions = new ArrayList<ActionButton>();
        final var held = DialogSupport.useHeldBlockButton((audience, block) -> {
            line.setBlock(block);
            DialogSupport.show(audience, current -> EditBlockLineVisualsDialog.create(hologram, lineIndex, line, note, current));
        });
        if (held != null) actions.add(held);
        actions.add(DialogSupport.visualGlowButton(hologram, line, audience -> EditBlockLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        actions.add(DialogSupport.visualGlowColorButton(hologram, line, audience -> EditBlockLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        actions.add(DialogSupport.visualBillboardButton(hologram, line, audience -> EditBlockLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        actions.add(DialogSupport.visualOffsetButton(hologram, line, audience -> EditBlockLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        actions.add(DialogSupport.visualDisplayScaleButton(line, audience -> EditBlockLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        actions.add(DialogSupport.visualBrightnessButton(hologram, line, audience -> EditBlockLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        return VisualOptionsDialog.create("Visual Options", DialogSupport.lineLabel(lineIndex, line), note, actions, back);
    }
}
