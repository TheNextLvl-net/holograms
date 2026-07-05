package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class AddEntityLineDialog {
    private AddEntityLineDialog() {
    }

    static DialogLike create(final Hologram hologram, final String initial, @Nullable final Component note) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> AddLineTypeDialog.create(hologram));
                }))).build();
        return EntitySearchDialog.create("Add Entity Line", initial, note, back, (audience, entityType) -> {
            hologram.addEntityLine(entityType);
            DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
        });
    }
}
