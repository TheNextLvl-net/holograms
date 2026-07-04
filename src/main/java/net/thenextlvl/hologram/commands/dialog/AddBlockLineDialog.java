package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
final class AddBlockLineDialog {
    private AddBlockLineDialog() {
    }

    static DialogLike create(final Hologram hologram, final String initial, @Nullable final Component note) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> AddLineTypeDialog.create(hologram));
                }))).build();
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
