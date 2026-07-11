package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.text.Component;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
final class AddBlockLineDialog {
    private AddBlockLineDialog() {
    }

    static Dialog<?> create(final Hologram hologram, final String initial, @Nullable final Component note) {
        final var back = BackButton.create(ignored -> AddLineTypeDialog.create(hologram));
        final var held = DialogSupport.useHeldBlockButton((audience, block) -> {
            hologram.addBlockLine().setBlock(block);
            DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
        });
        return BlockSearchDialog.create("Add Block Line", initial, note, List.of(held), back, (audience, block) -> {
            hologram.addBlockLine().setBlock(block);
            DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
        });
    }
}
