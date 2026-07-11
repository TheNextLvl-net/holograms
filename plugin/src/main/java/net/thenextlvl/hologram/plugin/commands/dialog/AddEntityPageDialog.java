package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.text.Component;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class AddEntityPageDialog {
    private AddEntityPageDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final String initial,
            @Nullable final Component note
    ) {
        return EntitySearchDialog.create("Add Entity Page", initial, note, DialogSupport.editPagedBackButton(hologram, lineIndex, line),
                (audience, entityType) -> {
                    line.addEntityPage(entityType);
                    DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
                });
    }
}
