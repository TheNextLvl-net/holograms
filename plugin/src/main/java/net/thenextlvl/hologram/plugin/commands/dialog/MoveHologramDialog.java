package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.thenextlvl.hologram.Hologram;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class MoveHologramDialog {
    private MoveHologramDialog() {
    }

    static DialogLike create(final Hologram hologram, final Audience viewer) {
        final var confirm = ActionButton.builder(Component.text("Yes"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    if (!(audience instanceof final Player player)) {
                        DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                        return;
                    }

                    hologram.teleportAsync(player.getLocation());
                    DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                }))).build();

        final var cancel = ActionButton.builder(Component.text("No"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                }))).build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Move " + hologram.getName() + " here?"))
                        .build())
                .type(DialogType.confirmation(confirm, cancel)));
    }
}
