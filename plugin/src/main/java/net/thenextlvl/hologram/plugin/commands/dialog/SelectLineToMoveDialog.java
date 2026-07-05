package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class SelectLineToMoveDialog {
    private SelectLineToMoveDialog() {
    }

    static DialogLike create(final Hologram hologram, final Audience viewer) {
        return SelectLineDialog.create(hologram, viewer, "Select line to move",
                "Select the line that should be moved.", -1,
                audience -> ChangeLineOrderDialog.create(hologram, audience),
                lineIndex -> audience -> {
                    DialogSupport.show(audience, current -> SelectLineMoveTargetDialog.create(hologram, lineIndex, current));
                });
    }
}
