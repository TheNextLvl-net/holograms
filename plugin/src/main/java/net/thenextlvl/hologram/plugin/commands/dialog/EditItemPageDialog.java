package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

@NullMarked
final class EditItemPageDialog {
    private EditItemPageDialog() {
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
        final var actions = new ArrayList<ActionButton>();
        final var setHeld = DialogSupport.heldItemButton(viewer, "Set to Held Item", (audience, item) -> {
            page.setItemStack(item);
            DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, pagedLine, current));
        });
        if (setHeld != null) actions.add(setHeld);
        final var visual = ActionButton.builder(Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditItemPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, current));
                }))).build();
        final var playerHead = ActionButton.builder(Component.text(
                        "Player head: " + (page.isPlayerHead() ? "On" : "Off"),
                        page.isPlayerHead() ? NamedTextColor.GREEN : NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    page.setPlayerHead(!page.isPlayerHead());
                    DialogSupport.show(audience, current -> EditItemPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, current));
                }))).build();
        final var actionsButton = DialogSupport.clickActionsButton(hologram, page, Component.text("Page " + (pageIndex + 1)), note,
                audience -> EditItemPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, viewer));
        actions.add(visual);
        actions.add(playerHead);
        actions.add(actionsButton);
        final var delete = DialogSupport.deletePageButton(hologram, lineIndex, pagedLine, pageIndex, false);
        actions.add(delete);
        return ItemSearchDialog.create("Page " + (pageIndex + 1), page.getItemStack().getType().key().asString(), note,
                actions, DialogSupport.editPageBackButton(hologram, lineIndex, pagedLine), (audience, item) -> {
                    page.setItemStack(item);
                    DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, pagedLine, current));
                });
    }
}
