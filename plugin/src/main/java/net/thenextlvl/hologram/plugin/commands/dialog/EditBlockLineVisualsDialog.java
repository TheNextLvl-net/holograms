package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.BlockHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

@NullMarked
final class EditBlockLineVisualsDialog {
    private EditBlockLineVisualsDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final BlockHologramLine line,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var back = BackButton.create(ignored -> EditBlockLineDialog.create(hologram, lineIndex, line, note));

        final var actions = new ArrayList<Button<?>>();
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
