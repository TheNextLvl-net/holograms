package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class TeleportHologramDialog {
    private TeleportHologramDialog() {
    }

    static Dialog<?> create(final Hologram hologram) {
        return TeleportHologramDialog.create(hologram, hologram.getLocation(), null);
    }

    static Dialog<?> create(final Hologram hologram, final Location location) {
        return TeleportHologramDialog.create(hologram, location, null);
    }

    static Dialog<?> create(final Hologram hologram, final Location location, @Nullable final Component note) {
        return TeleportHologramDialog.create(hologram, location, DialogSupport.locationInputs(location), note);
    }

    static Dialog<?> create(
            final Hologram hologram,
            final Location location,
            final DialogSupport.LocationInputs inputs,
            @Nullable final Component note
    ) {
        final var teleportPlayer = Button.callback((response, audience) -> {
            if (!(audience instanceof final Player player)) {
                DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                return;
            }

            player.teleportAsync(hologram.getLocation()).thenRun(() -> {
                final var parsed = DialogSupport.parseLocation(location,
                        response.getText("world"), response.getText("x"), response.getText("y"), response.getText("z"),
                        response.getText("yaw"), response.getText("pitch"));
                final var currentInputs = DialogSupport.locationInputs(response);
                DialogSupport.show(audience, ignored -> TeleportHologramDialog.create(hologram, parsed.value() != null ? parsed.value() : location, currentInputs,
                        parsed.error() != null ? Component.text(parsed.error(), NamedTextColor.RED) : null));
            });
        }, Component.text("Teleport to Hologram", NamedTextColor.AQUA)).uses(1).width(300);
        final var moveHere = Button.clickEvent(ClickEvent.callback(audience -> {
            if (audience instanceof final Player player) {
                final var snapshot = player.getLocation().clone();
                DialogSupport.show(audience, ignored -> TeleportHologramDialog.create(hologram, snapshot));
                return;
            }
            DialogSupport.show(audience, ignored -> TeleportHologramDialog.create(hologram));
        }), Component.text("Move Here", NamedTextColor.AQUA)).width(300);
        final var move = Button.callback((response, audience) -> {
            final var target = DialogSupport.parseLocation(hologram.getLocation(), response.getText("world"), response.getText("x"), response.getText("y"),
                    response.getText("z"), response.getText("yaw"), response.getText("pitch"));
            if (target.error() != null) {
                final var currentInputs = DialogSupport.locationInputs(response);
                DialogSupport.show(audience, ignored -> TeleportHologramDialog.create(hologram, location, currentInputs, Component.text(target.error(), NamedTextColor.RED)));
                return;
            }

            hologram.teleportAsync(target.value());
            DialogSupport.show(audience, ignored -> TeleportHologramDialog.create(hologram, target.value()));
        }, Component.text("Commence Teleport", NamedTextColor.GREEN)).uses(1).width(300);

        final var dialog = Dialog.multiAction()
                .title(Component.text("Teleport Hologram"))
                .addInput(DialogSupport.locationInput("world", "World", inputs.world()))
                .addInput(DialogSupport.locationInput("x", "X", inputs.x()))
                .addInput(DialogSupport.locationInput("y", "Y", inputs.y()))
                .addInput(DialogSupport.locationInput("z", "Z", inputs.z()))
                .addInput(DialogSupport.locationInput("yaw", "Yaw", inputs.yaw()))
                .addInput(DialogSupport.locationInput("pitch", "Pitch", inputs.pitch()))
                .addButton(teleportPlayer)
                .addButton(moveHere)
                .addButton(move)
                .addButton(BackButton.create(current -> EditHologramDialog.create(hologram, current)).width(300))
                .columns(1);
        if (note != null) dialog.addBody(Body.text(note));
        return dialog;
    }
}
