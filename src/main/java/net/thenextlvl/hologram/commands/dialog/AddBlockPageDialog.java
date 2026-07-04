package net.thenextlvl.hologram.commands.dialog;

import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
final class AddBlockPageDialog {
    private AddBlockPageDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final String initial,
            @Nullable final Component note
    ) {
        final var held = DialogSupport.useHeldBlockButton((audience, block) -> {
            line.addBlockPage().setBlock(block);
            DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
        });
        return BlockSearchDialog.create("Add Block Page", initial, note, List.of(held), DialogSupport.editPagedBackButton(hologram, lineIndex, line),
                (audience, block) -> {
                    line.addBlockPage().setBlock(block);
                    DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
                });
    }
}
