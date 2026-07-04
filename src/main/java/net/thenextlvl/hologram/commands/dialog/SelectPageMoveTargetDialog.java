package net.thenextlvl.hologram.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class SelectPageMoveTargetDialog {
    private SelectPageMoveTargetDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final int from,
            final Audience viewer
    ) {
        return SelectPageDialog.create(hologram, lineIndex, line, viewer, "Move above page",
                "Select the target page. The moved page will be placed directly above it.", from,
                audience -> SelectPageToMoveDialog.create(hologram, lineIndex, line, audience),
                target -> audience -> {
                    if (target == from) {
                        DialogSupport.show(audience, ignored -> SelectPageMoveTargetDialog.create(hologram, lineIndex, line, from, viewer,
                                Component.text("You cannot move a page above itself", NamedTextColor.RED)));
                        return;
                    }
                    line.movePage(from, from < target ? target - 1 : target);
                    DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
                });
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final int from,
            final Audience viewer,
            @Nullable final Component note
    ) {
        return SelectPageDialog.create(hologram, lineIndex, line, viewer, "Move above page",
                "Select the target page. The moved page will be placed directly above it.", from,
                audience -> SelectPageToMoveDialog.create(hologram, lineIndex, line, audience), note,
                target -> audience -> {
                    if (target == from) {
                        DialogSupport.show(audience, ignored -> SelectPageMoveTargetDialog.create(hologram, lineIndex, line, from, viewer,
                                Component.text("You cannot move a page above itself", NamedTextColor.RED)));
                        return;
                    }
                    line.movePage(from, from < target ? target - 1 : target);
                    DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
                });
    }
}
