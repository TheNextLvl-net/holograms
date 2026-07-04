package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
final class EditBlockPageDialog {
    private EditBlockPageDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final BlockHologramLine page,
            @Nullable final Component note
    ) {
        final var held = DialogSupport.useHeldBlockButton((audience, block) -> {
            page.setBlock(block);
            DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, pagedLine, current));
        });
        final var visual = ActionButton.builder(Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditBlockPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note, current));
                }))).build();
        final var actionsButton = DialogSupport.clickActionsButton(hologram, page, Component.text("Page " + (pageIndex + 1)), note,
                audience -> EditBlockPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, note));
        final var delete = DialogSupport.deletePageButton(hologram, lineIndex, pagedLine, pageIndex, false);
        return BlockSearchDialog.create("Page " + (pageIndex + 1), page.getBlock().getMaterial().key().asString(), note,
                List.of(held, visual, actionsButton, delete), DialogSupport.editPageBackButton(hologram, lineIndex, pagedLine), (audience, block) -> {
                    page.setBlock(block);
                    DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, pagedLine, current));
                });
    }
}
