package net.thenextlvl.hologram.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class SelectLineMoveTargetDialog {
    private SelectLineMoveTargetDialog() {
    }

    static DialogLike create(final Hologram hologram, final int from, final Audience viewer) {
        return SelectLineDialog.create(hologram, viewer, "Move above line",
                "Select the target line. The moved line will be placed directly above it.", from,
                audience -> SelectLineToMoveDialog.create(hologram, audience),
                target -> audience -> {
                    if (target == from) {
                        DialogSupport.show(audience, ignored -> SelectLineMoveTargetDialog.create(hologram, from, viewer,
                                Component.text("You cannot move a line above itself", NamedTextColor.RED)));
                        return;
                    }
                    hologram.moveLine(from, from < target ? target - 1 : target);
                    DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                });
    }

    static DialogLike create(
            final Hologram hologram,
            final int from,
            final Audience viewer,
            @Nullable final Component note
    ) {
        return SelectLineDialog.create(hologram, viewer, "Move above line",
                "Select the target line. The moved line will be placed directly above it.", from,
                audience -> SelectLineToMoveDialog.create(hologram, audience), note,
                target -> audience -> {
                    if (target == from) {
                        DialogSupport.show(audience, ignored -> SelectLineMoveTargetDialog.create(hologram, from, viewer,
                                Component.text("You cannot move a line above itself", NamedTextColor.RED)));
                        return;
                    }
                    hologram.moveLine(from, from < target ? target - 1 : target);
                    DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                });
    }
}
