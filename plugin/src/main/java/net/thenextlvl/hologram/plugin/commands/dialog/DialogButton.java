package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.button.ClickEventButton;
import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

@NullMarked
final class DialogButton {
    private DialogButton() {
    }

    static ClickEventButton create(final Function<Audience, Dialog<?>> dialog, final Component label) {
        return Button.clickEvent(ClickEvent.callback(audience -> DialogSupport.show(audience, dialog)), label);
    }
}
