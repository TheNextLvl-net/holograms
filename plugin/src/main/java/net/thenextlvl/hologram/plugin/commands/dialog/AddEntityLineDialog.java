package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.text.Component;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class AddEntityLineDialog {
    private AddEntityLineDialog() {
    }

    static Dialog<?> create(final Hologram hologram, final String initial, @Nullable final Component note) {
        final var back = BackButton.create(ignored -> AddLineTypeDialog.create(hologram));
        return EntitySearchDialog.create("Add Entity Line", initial, note, back, (audience, entityType) -> {
            hologram.addEntityLine(entityType);
            DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
        });
    }
}
