package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class MoveHologramDialog {
    private MoveHologramDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(final Hologram hologram, final Audience viewer) {
        final var confirm = Button.clickEvent(ClickEvent.callback(audience -> {
            if (!(audience instanceof final Player player)) {
                DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                return;
            }

            hologram.teleportAsync(player.getLocation());
            DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
        }), Component.text("Yes"));

        final var cancel = Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
        }), Component.text("No"));

        final var dialog = Dialog.confirmation(cancel, confirm).title(Component.text("Move " + hologram.getName() + " here?"));
        return dialog;
    }
}
