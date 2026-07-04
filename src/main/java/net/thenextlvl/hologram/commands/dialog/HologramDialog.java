package net.thenextlvl.hologram.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class HologramDialog {
    private HologramDialog() {
    }

    public static void showLast(final Audience audience) {
        DialogSupport.showLast(audience);
    }

    public static DialogLike overview() {
        return OverviewDialog.create();
    }
}
