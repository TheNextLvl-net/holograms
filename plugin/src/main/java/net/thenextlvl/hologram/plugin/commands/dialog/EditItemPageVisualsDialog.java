package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

@NullMarked
final class EditItemPageVisualsDialog {
    private EditItemPageVisualsDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final ItemHologramLine page,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var back = BackButton.create(current -> EditItemPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, current));

        final var actions = new ArrayList<Button<?>>();
        final var held = DialogSupport.heldItemButton(viewer, "Set to Held Item", (audience, item) -> {
            page.setItemStack(item);
            DialogSupport.show(audience, current -> EditItemPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, current));
        });
        if (held != null) actions.add(held);
        actions.add(DialogSupport.toggleButton("Player Head", page.isPlayerHead(), (audience, value) -> {
            page.setPlayerHead(value);
            DialogSupport.show(audience, current -> EditItemPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, current));
        }));
        actions.add(DialogSupport.enumButton("Item Display", page.getItemDisplayTransform(), ItemDisplayTransform.class, page::setItemDisplayTransform,
                audience -> EditItemPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(DialogSupport.visualGlowColorButton(hologram, page, audience -> EditItemPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(DialogSupport.visualOffsetButton(hologram, page, audience -> EditItemPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(DialogSupport.visualDisplayScaleButton(page, audience -> EditItemPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(DialogSupport.visualBrightnessButton(hologram, page, audience -> EditItemPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        return VisualOptionsDialog.create(Component.text("Page " + (pageIndex + 1)), note, actions, back)
                .addInput(DialogSupport.visualGlowButton(hologram, page, audience -> EditItemPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)))
                .addInput(DialogSupport.visualBillboardButton(hologram, page, audience -> EditItemPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
    }
}
