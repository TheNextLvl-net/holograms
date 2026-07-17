package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;

@NullMarked
final class EditTextLineVisualsDialog {
    private EditTextLineVisualsDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final TextHologramLine line,
            final Audience viewer
    ) {
        final var back = BackButton.create(ignored -> EditTextLineDialog.create(hologram, lineIndex, line, line.getUnparsedText().orElse(""), null));

        final var actions = new ArrayList<Button<?>>();
        actions.add(DialogSupport.visualGlowColorButton(hologram, line, audience -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, audience)));
        actions.add(DialogSupport.visualOffsetButton(hologram, line, audience -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, audience)));
        actions.add(DialogSupport.visualDisplayScaleButton(line, audience -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, audience)));
        actions.add(DialogSupport.visualBrightnessButton(hologram, line, audience -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, audience)));
        actions.add(DialogSupport.visualFloatButton("View Range", line.getViewRange(), 0.0d, 1000.0d, (audience, value) -> {
            line.setViewRange(value.floatValue());
            DialogSupport.show(audience, current -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, current));
        }, audience -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, audience)));
        actions.add(DialogSupport.visualFloatButton("Shadow Radius", line.getShadowRadius(), 0.0d, 1000.0d, (audience, value) -> {
            line.setShadowRadius(value.floatValue());
            DialogSupport.show(audience, current -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, current));
        }, audience -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, audience)));
        actions.add(DialogSupport.visualFloatButton("Shadow Strength", line.getShadowStrength(), 0.0d, 10.0d, (audience, value) -> {
            line.setShadowStrength(value.floatValue());
            DialogSupport.show(audience, current -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, current));
        }, audience -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, audience)));
        actions.add(DialogSupport.visualFloatButton("Display Width", line.getDisplayWidth(), 0.0d, 4096.0d, (audience, value) -> {
            line.setDisplayWidth(value.floatValue());
            DialogSupport.show(audience, current -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, current));
        }, audience -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, audience)));
        actions.add(DialogSupport.visualFloatButton("Display Height", line.getDisplayHeight(), 0.0d, 4096.0d, (audience, value) -> {
            line.setDisplayHeight(value.floatValue());
            DialogSupport.show(audience, current -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, current));
        }, audience -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, audience)));
        actions.add(DialogSupport.visualIntButton("Interpolation Delay", line.getInterpolationDelay(), 0, 6000, (audience, value) -> {
            line.setInterpolationDelay(value);
            DialogSupport.show(audience, current -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, current));
        }, audience -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, audience)));
        actions.add(DialogSupport.visualIntButton("Interpolation Duration", line.getInterpolationDuration(), 0, 6000, (audience, value) -> {
            line.setInterpolationDuration(value);
            DialogSupport.show(audience, current -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, current));
        }, audience -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, audience)));
        actions.add(DialogSupport.visualIntButton("Teleport Duration", line.getTeleportDuration(), 0, 59, (audience, value) -> {
            line.setTeleportDuration(value);
            DialogSupport.show(audience, current -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, current));
        }, audience -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, audience)));
        actions.add(DialogSupport.visualBackgroundColorButton("Background Color", line.getBackgroundColor().orElse(null), true, (audience, color) -> {
            line.setBackgroundColor(color);
            DialogSupport.show(audience, current -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, current));
        }, audience -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, audience)));
        actions.add(DialogSupport.visualLineWidthButton(line.getLineWidth(), (audience, value) -> {
            line.setLineWidth(value);
            DialogSupport.show(audience, current -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, current));
        }, audience -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, audience)));
        actions.add(DialogSupport.visualIntButton("Text Opacity", line.getTextOpacity(), 0, 100, (audience, value) -> {
            line.setTextOpacity(value);
            DialogSupport.show(audience, current -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, current));
        }, audience -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, audience)));
        actions.add(DialogSupport.toggleButton("Shadowed", line.isShadowed(), (audience, value) -> {
            line.setShadowed(value);
            DialogSupport.show(audience, current -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, current));
        }));
        actions.add(DialogSupport.toggleButton("See Through", line.isSeeThrough(), (audience, value) -> {
            line.setSeeThrough(value);
            DialogSupport.show(audience, current -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, current));
        }));
        actions.add(DialogSupport.toggleButton("Default Background", line.isDefaultBackground(), (audience, value) -> {
            line.setDefaultBackground(value);
            DialogSupport.show(audience, current -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, current));
        }));
        actions.add(DialogSupport.enumButton("Alignment", line.getAlignment(), TextAlignment.class, line::setAlignment,
                audience -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, audience)));
        return VisualOptionsDialog.create(DialogSupport.lineLabel(lineIndex, line), null, actions, back)
                .addInput(DialogSupport.visualGlowButton(hologram, line, audience -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, audience)))
                .addInput(DialogSupport.visualBillboardButton(hologram, line, audience -> EditTextLineVisualsDialog.create(hologram, lineIndex, line, audience)));
    }
}
