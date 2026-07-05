package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class EditEntityPageDialog {
    private EditEntityPageDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final EntityHologramLine page,
            @Nullable final Component note
    ) {
        final var visual = ActionButton.builder(Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditEntityPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, current));
                }))).build();
        final var actionsButton = DialogSupport.clickActionsButton(hologram, page, Component.text("Page " + (pageIndex + 1)), note,
                audience -> EditEntityPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note));
        return EntitySearchDialog.create("Page " + (pageIndex + 1), page.getEntityType().key().asString(), note,
                DialogSupport.editPageBackButton(hologram, lineIndex, pagedLine), (audience, entityType) -> {
                    page.setEntityType(entityType);
                    DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, pagedLine, current));
                }, visual, actionsButton, DialogSupport.deletePageButton(hologram, lineIndex, pagedLine, pageIndex, false));
    }
}
