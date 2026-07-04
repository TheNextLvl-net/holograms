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
final class SelectPageSwapTargetDialog {
    private SelectPageSwapTargetDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final int first,
            final Audience viewer
    ) {
        return SelectPageDialog.create(hologram, lineIndex, line, viewer, "Select second page",
                "Select the second page. The two selected pages will swap positions.", first,
                audience -> SelectPageToSwapDialog.create(hologram, lineIndex, line, audience),
                second -> audience -> {
                    if (second == first) {
                        DialogSupport.show(audience, ignored -> SelectPageSwapTargetDialog.create(hologram, lineIndex, line, first, viewer,
                                Component.text("You cannot swap a page with itself", NamedTextColor.RED)));
                        return;
                    }
                    line.swapPages(first, second);
                    DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
                });
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final int first,
            final Audience viewer,
            @Nullable final Component note
    ) {
        return SelectPageDialog.create(hologram, lineIndex, line, viewer, "Select second page",
                "Select the second page. The two selected pages will swap positions.", first,
                audience -> SelectPageToSwapDialog.create(hologram, lineIndex, line, audience), note,
                second -> audience -> {
                    if (second == first) {
                        DialogSupport.show(audience, ignored -> SelectPageSwapTargetDialog.create(hologram, lineIndex, line, first, viewer,
                                Component.text("You cannot swap a page with itself", NamedTextColor.RED)));
                        return;
                    }
                    line.swapPages(first, second);
                    DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
                });
    }
}
