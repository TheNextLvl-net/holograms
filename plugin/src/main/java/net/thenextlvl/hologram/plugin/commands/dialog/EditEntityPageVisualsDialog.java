package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

@NullMarked
final class EditEntityPageVisualsDialog {
    private EditEntityPageVisualsDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final EntityHologramLine page,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var back = BackButton.create(current -> EditEntityPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note));

        final var actions = new ArrayList<Button<?>>();
        actions.add(DialogSupport.visualGlowButton(hologram, page, audience -> EditEntityPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(DialogSupport.visualGlowColorButton(hologram, page, audience -> EditEntityPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(DialogSupport.visualBillboardButton(hologram, page, audience -> EditEntityPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(DialogSupport.visualOffsetButton(hologram, page, audience -> EditEntityPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(DialogSupport.visualScaleButton(page.getScale(), 0.1d, 100.0d, (audience, value) -> {
            page.setScale(value);
            DialogSupport.show(audience, current -> EditEntityPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, current));
        }, audience -> EditEntityPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        return VisualOptionsDialog.create("Visual Options", Component.text("Page " + (pageIndex + 1)), note, actions, back);
    }
}
