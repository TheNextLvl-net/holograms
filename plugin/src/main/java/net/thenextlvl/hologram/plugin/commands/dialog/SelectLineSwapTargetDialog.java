package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class SelectLineSwapTargetDialog {
    private SelectLineSwapTargetDialog() {
    }

    static DialogLike create(final Hologram hologram, final int first, final Audience viewer) {
        return SelectLineDialog.create(hologram, viewer, "Select second line",
                "Select the second line. The two selected lines will swap positions.", first,
                audience -> SelectLineToSwapDialog.create(hologram, audience),
                second -> audience -> {
                    if (second == first) {
                        DialogSupport.show(audience, ignored -> SelectLineSwapTargetDialog.create(hologram, first, viewer,
                                Component.text("You cannot swap a line with itself", NamedTextColor.RED)));
                        return;
                    }
                    hologram.swapLines(first, second);
                    DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                });
    }

    static DialogLike create(
            final Hologram hologram,
            final int first,
            final Audience viewer,
            @Nullable final Component note
    ) {
        return SelectLineDialog.create(hologram, viewer, "Select second line",
                "Select the second line. The two selected lines will swap positions.", first,
                audience -> SelectLineToSwapDialog.create(hologram, audience), note,
                second -> audience -> {
                    if (second == first) {
                        DialogSupport.show(audience, ignored -> SelectLineSwapTargetDialog.create(hologram, first, viewer,
                                Component.text("You cannot swap a line with itself", NamedTextColor.RED)));
                        return;
                    }
                    hologram.swapLines(first, second);
                    DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                });
    }
}
