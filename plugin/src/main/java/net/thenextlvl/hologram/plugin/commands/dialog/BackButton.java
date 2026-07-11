package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.ClickEventButton;
import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

@NullMarked
final class BackButton {
    private BackButton() {
    }

    static ClickEventButton create(final Function<Audience, Dialog<?>> dialog) {
        return DialogButton.create(dialog, Component.text("Back"));
    }
}
