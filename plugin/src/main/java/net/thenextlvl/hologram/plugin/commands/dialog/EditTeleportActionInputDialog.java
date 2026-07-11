package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.line.HologramLine;
import org.bukkit.Location;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@NullMarked
final class EditTeleportActionInputDialog {
    private EditTeleportActionInputDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<Location> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, net.thenextlvl.dialogs.Dialog<?>> reopen
    ) {
        final var current = action.getInput();
        final var save = Button.callback((response, audience) -> {
            final var locationInputs = DialogSupport.locationInputs(response);
            final var parsed = DialogSupport.parseLocation(current, locationInputs.world(), locationInputs.x(), locationInputs.y(), locationInputs.z(),
                    locationInputs.yaw(), locationInputs.pitch());
            if (parsed.error() != null) {
                DialogSupport.show(audience, ignored -> EditTeleportActionInputDialog.create(hologram, line, actionName, action, header, Component.text(parsed.error(), NamedTextColor.RED), reopen));
                return;
            }
            action.setInput(parsed.value());
            DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen));
        }, Component.text("Save", NamedTextColor.GREEN)).uses(1);
        final var back = BackButton.create(ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen));
        final var body = new ArrayList<DialogBody>();
        body.add(Body.text(Component.text("Edit the target location")));
        if (note != null) body.add(Body.text(note));
        final var inputs = DialogSupport.locationInputs(current);
        final var dialog = Dialog.multiAction().title(Component.text("Teleport"));
        body.forEach(dialog::addBody);
        List.of(
                DialogSupport.locationInput("world", "World", inputs.world()),
                DialogSupport.locationInput("x", "X", inputs.x()),
                DialogSupport.locationInput("y", "Y", inputs.y()),
                DialogSupport.locationInput("z", "Z", inputs.z()),
                DialogSupport.locationInput("yaw", "Yaw", inputs.yaw()),
                DialogSupport.locationInput("pitch", "Pitch", inputs.pitch())
        ).forEach(dialog::addInput);
        List.of(save).forEach(dialog::addButton);
        dialog.exitAction(back);
        return dialog;
    }
}
