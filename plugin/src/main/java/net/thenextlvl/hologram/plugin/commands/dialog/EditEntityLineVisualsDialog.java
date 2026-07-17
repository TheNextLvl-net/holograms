package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.EntityHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

@NullMarked
final class EditEntityLineVisualsDialog {
    private EditEntityLineVisualsDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final EntityHologramLine line,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var back = BackButton.create(ignored -> EditEntityLineDialog.create(hologram, lineIndex, line, note));

        final var actions = new ArrayList<Button<?>>();
        actions.add(DialogSupport.visualGlowColorButton(hologram, line, audience -> EditEntityLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        actions.add(DialogSupport.visualOffsetButton(hologram, line, audience -> EditEntityLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
        return VisualOptionsDialog.create(DialogSupport.lineLabel(lineIndex, line), note, actions, back)
                .addInput(DialogSupport.visualGlowButton(hologram, line, audience -> EditEntityLineVisualsDialog.create(hologram, lineIndex, line, note, audience)))
                .addInput(DialogSupport.visualScaleButton((float) line.getScale(), .1F, 100F, (audience, value) -> {
                    line.setScale(value);
                    DialogSupport.show(audience, current -> EditEntityLineVisualsDialog.create(hologram, lineIndex, line, note, current));
                }, audience -> EditEntityLineVisualsDialog.create(hologram, lineIndex, line, note, audience)))
                .addInput(DialogSupport.visualBillboardButton(hologram, line, audience -> EditEntityLineVisualsDialog.create(hologram, lineIndex, line, note, audience)));
    }
}
