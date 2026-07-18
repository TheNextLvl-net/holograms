package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;

@NullMarked
final class EditTextPageVisualsDialog {
    private EditTextPageVisualsDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final TextHologramLine page,
            final Audience viewer
    ) {
        final var back = BackButton.create(ignored -> EditTextPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, null));

        final var actions = new ArrayList<Button<?>>();
        actions.add(DialogSupport.visualGlowColorButton(hologram, page, audience -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(DialogSupport.visualOffsetButton(hologram, page, audience -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(DialogSupport.visualDisplayScaleButton(page, audience -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(DialogSupport.visualBrightnessButton(hologram, page, audience -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(DialogSupport.visualFloatButton("View Range", page.getViewRange(), 0.0d, 1000.0d, (audience, value) -> {
            page.setViewRange(value.floatValue());
            DialogSupport.show(audience, current -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(DialogSupport.visualFloatButton("Shadow Radius", page.getShadowRadius(), 0.0d, 1000.0d, (audience, value) -> {
            page.setShadowRadius(value.floatValue());
            DialogSupport.show(audience, current -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(DialogSupport.visualFloatButton("Shadow Strength", page.getShadowStrength(), 0.0d, 10.0d, (audience, value) -> {
            page.setShadowStrength(value.floatValue());
            DialogSupport.show(audience, current -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(DialogSupport.visualFloatButton("Display Width", page.getDisplayWidth(), 0.0d, 4096.0d, (audience, value) -> {
            page.setDisplayWidth(value.floatValue());
            DialogSupport.show(audience, current -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(DialogSupport.visualFloatButton("Display Height", page.getDisplayHeight(), 0.0d, 4096.0d, (audience, value) -> {
            page.setDisplayHeight(value.floatValue());
            DialogSupport.show(audience, current -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(DialogSupport.visualIntButton("Interpolation Delay", page.getInterpolationDelay(), 0, 6000, (audience, value) -> {
            page.setInterpolationDelay(value);
            DialogSupport.show(audience, current -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(DialogSupport.visualIntButton("Interpolation Duration", page.getInterpolationDuration(), 0, 6000, (audience, value) -> {
            page.setInterpolationDuration(value);
            DialogSupport.show(audience, current -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(DialogSupport.visualIntButton("Teleport Duration", page.getTeleportDuration(), 0, 59, (audience, value) -> {
            page.setTeleportDuration(value);
            DialogSupport.show(audience, current -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(DialogSupport.visualBackgroundColorButton("Background Color", page.getBackgroundColor().orElse(null), true, (audience, color) -> {
            page.setBackgroundColor(color);
            DialogSupport.show(audience, current -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(DialogSupport.visualLineWidthButton(page.getLineWidth(), (audience, value) -> {
            page.setLineWidth(value);
            DialogSupport.show(audience, current -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(DialogSupport.visualIntButton("Text Opacity", page.getTextOpacity(), 0, 100, (audience, value) -> {
            page.setTextOpacity(value);
            DialogSupport.show(audience, current -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(DialogSupport.toggleButton("Shadowed", page.isShadowed(), (audience, value) -> {
            page.setShadowed(value);
            DialogSupport.show(audience, current -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }));
        actions.add(DialogSupport.toggleButton("See Through", page.isSeeThrough(), (audience, value) -> {
            page.setSeeThrough(value);
            DialogSupport.show(audience, current -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }));
        actions.add(DialogSupport.toggleButton("Default Background", page.isDefaultBackground(), (audience, value) -> {
            page.setDefaultBackground(value);
            DialogSupport.show(audience, current -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }));
        actions.add(DialogSupport.enumButton("Alignment", page.getAlignment(), TextAlignment.class, page::setAlignment,
                audience -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        return VisualOptionsDialog.create(Component.text("Page " + (pageIndex + 1)), null, actions, back)
                .addInput(DialogSupport.visualBillboardButton(hologram, page, audience -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
    }
}
