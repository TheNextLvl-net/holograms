package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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

    static DialogLike create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<Location> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, DialogLike> reopen
    ) {
        final var current = action.getInput();
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var locationInputs = DialogSupport.locationInputs(response);
                    final var parsed = DialogSupport.parseLocation(current, locationInputs.world(), locationInputs.x(), locationInputs.y(), locationInputs.z(),
                            locationInputs.yaw(), locationInputs.pitch());
                    if (parsed.error() != null) {
                        DialogSupport.show(audience, ignored -> EditTeleportActionInputDialog.create(hologram, line, actionName, action, header, Component.text(parsed.error(), NamedTextColor.RED), reopen));
                        return;
                    }
                    action.setInput(parsed.value());
                    DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen));
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen)))))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Edit the target location")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        final var inputs = DialogSupport.locationInputs(current);
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Teleport"))
                        .body(body)
                        .inputs(List.of(
                                DialogSupport.locationInput("world", "World", inputs.world()),
                                DialogSupport.locationInput("x", "X", inputs.x()),
                                DialogSupport.locationInput("y", "Y", inputs.y()),
                                DialogSupport.locationInput("z", "Z", inputs.z()),
                                DialogSupport.locationInput("yaw", "Yaw", inputs.yaw()),
                                DialogSupport.locationInput("pitch", "Pitch", inputs.pitch())
                        ))
                        .build())
                .type(DialogType.multiAction(List.of(save)).exitAction(back).build()));
    }
}
