package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
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

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final ItemHologramLine page,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditItemPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, current));
                })))
                .build();

        final var actions = new ArrayList<ActionButton>();
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
        actions.add(DialogSupport.visualGlowButton(hologram, page, audience -> EditItemPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(DialogSupport.visualGlowColorButton(hologram, page, audience -> EditItemPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(DialogSupport.visualBillboardButton(hologram, page, audience -> EditItemPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(DialogSupport.visualOffsetButton(hologram, page, audience -> EditItemPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(DialogSupport.visualDisplayScaleButton(page, audience -> EditItemPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(DialogSupport.visualBrightnessButton(hologram, page, audience -> EditItemPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        return VisualOptionsDialog.create("Visual Options", Component.text("Page " + (pageIndex + 1)), note, actions, back);
    }
}
