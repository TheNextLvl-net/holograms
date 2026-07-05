package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
final class TeleportHologramDialog {
    private TeleportHologramDialog() {
    }

    static DialogLike create(final Hologram hologram) {
        return TeleportHologramDialog.create(hologram, hologram.getLocation(), null);
    }

    static DialogLike create(final Hologram hologram, final Location location) {
        return TeleportHologramDialog.create(hologram, location, null);
    }

    static DialogLike create(final Hologram hologram, final Location location, @Nullable final Component note) {
        return TeleportHologramDialog.create(hologram, location, DialogSupport.locationInputs(location), note);
    }

    static DialogLike create(
            final Hologram hologram,
            final Location location,
            final DialogSupport.LocationInputs inputs,
            @Nullable final Component note
    ) {
        final var teleportPlayer = ActionButton.builder(Component.text("Teleport to Hologram", NamedTextColor.AQUA))
                .action(DialogAction.customClick((response, audience) -> {
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
                }, ClickCallback.Options.builder().uses(1).build())).width(300).build();
        final var moveHere = ActionButton.builder(Component.text("Move Here", NamedTextColor.AQUA))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    if (audience instanceof final Player player) {
                        final var snapshot = player.getLocation().clone();
                        DialogSupport.show(audience, ignored -> TeleportHologramDialog.create(hologram, snapshot));
                        return;
                    }
                    DialogSupport.show(audience, ignored -> TeleportHologramDialog.create(hologram));
                }))).width(300).build();
        final var move = ActionButton.builder(Component.text("Commence Teleport", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var target = DialogSupport.parseLocation(hologram.getLocation(), response.getText("world"), response.getText("x"), response.getText("y"),
                            response.getText("z"), response.getText("yaw"), response.getText("pitch"));
                    if (target.error() != null) {
                        final var currentInputs = DialogSupport.locationInputs(response);
                        DialogSupport.show(audience, ignored -> TeleportHologramDialog.create(hologram, location, currentInputs, Component.text(target.error(), NamedTextColor.RED)));
                        return;
                    }

                    hologram.teleportAsync(target.value());
                    DialogSupport.show(audience, ignored -> TeleportHologramDialog.create(hologram, target.value()));
                }, ClickCallback.Options.builder().uses(1).build()))
                .width(300).build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                }))).width(300).build();

        final var body = new ArrayList<DialogBody>();
        if (note != null) body.add(DialogBody.plainMessage(note));

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Teleport Hologram"))
                        .body(body)
                        .inputs(List.of(
                                DialogSupport.locationInput("world", "World", inputs.world()),
                                DialogSupport.locationInput("x", "X", inputs.x()),
                                DialogSupport.locationInput("y", "Y", inputs.y()),
                                DialogSupport.locationInput("z", "Z", inputs.z()),
                                DialogSupport.locationInput("yaw", "Yaw", inputs.yaw()),
                                DialogSupport.locationInput("pitch", "Pitch", inputs.pitch())
                        )).build())
                .type(DialogType.multiAction(List.of(teleportPlayer, moveHere, move, back)).columns(1).build()));
    }
}
