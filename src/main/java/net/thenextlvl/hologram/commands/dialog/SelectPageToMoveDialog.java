package net.thenextlvl.hologram.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class SelectPageToMoveDialog {
    private SelectPageToMoveDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final Audience viewer
    ) {
        return SelectPageDialog.create(hologram, lineIndex, line, viewer, "Select page to move",
                "Select the page that should be moved.", -1,
                audience -> ChangePageOrderDialog.create(hologram, lineIndex, line, audience),
                pageIndex -> audience -> {
                    DialogSupport.show(audience, current -> SelectPageMoveTargetDialog.create(hologram, lineIndex, line, pageIndex, current));
                });
    }
}
