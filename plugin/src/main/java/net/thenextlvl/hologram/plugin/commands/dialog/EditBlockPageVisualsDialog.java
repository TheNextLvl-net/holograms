package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

@NullMarked
final class EditBlockPageVisualsDialog {
    private EditBlockPageVisualsDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final BlockHologramLine page,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var back = BackButton.create(current -> EditBlockPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note));

        final var actions = new ArrayList<Button<?>>();
        final var held = DialogSupport.useHeldBlockButton((audience, block) -> {
            page.setBlock(block);
            DialogSupport.show(audience, current -> EditBlockPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, current));
        });
        if (held != null) actions.add(held);
        actions.add(DialogSupport.visualGlowButton(hologram, page, audience -> EditBlockPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(DialogSupport.visualGlowColorButton(hologram, page, audience -> EditBlockPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(DialogSupport.visualBillboardButton(hologram, page, audience -> EditBlockPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(DialogSupport.visualOffsetButton(hologram, page, audience -> EditBlockPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(DialogSupport.visualDisplayScaleButton(page, audience -> EditBlockPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(DialogSupport.visualBrightnessButton(hologram, page, audience -> EditBlockPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        return VisualOptionsDialog.create("Visual Options", Component.text("Page " + (pageIndex + 1)), note, actions, back);
    }
}
