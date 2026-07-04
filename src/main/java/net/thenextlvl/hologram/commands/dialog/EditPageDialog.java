package net.thenextlvl.hologram.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditPageDialog {
    private EditPageDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final Audience viewer
    ) {
        final var page = pagedLine.getPage(pageIndex).orElse(null);
        return switch (page) {
            case null -> EditPagedLineDialog.create(hologram, lineIndex, pagedLine, viewer);
            case final TextHologramLine textLine ->
                    EditTextPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, textLine, null);
            case final BlockHologramLine blockLine ->
                    EditBlockPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, blockLine, null);
            case final ItemHologramLine itemLine ->
                    EditItemPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, itemLine, null, viewer);
            case final EntityHologramLine entityLine ->
                    EditEntityPageDialog.create(hologram, lineIndex, pagedLine, pageIndex, entityLine, null);
            default -> EditPagedLineDialog.create(hologram, lineIndex, pagedLine, viewer);
        };
    }
}
