package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramProvider;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public final class HologramDialog {
    public static DialogLike overview() {
        final var close = ActionButton.builder(Component.text("Close")).build();

        final var create = ActionButton.builder(Component.text("New Hologram"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(createNewHologram("", null));
                }))).build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Manage your Holograms"))
                        .body(List.of(DialogBody.plainMessage(Component.text("Hello"))))
                        .build())
                .type(DialogType.multiAction(List.of(create)).exitAction(close).build()));
    }

    private static DialogLike createNewHologram(final String initial, @Nullable final Component note) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(overview());
                })))
                .build();

        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter the name of the new hologram")));
        if (note != null) body.add(DialogBody.plainMessage(note));

        final var create = ActionButton.builder(Component.text("Create"))
                .action(DialogAction.customClick((response, audience) -> {
                    if (!(audience instanceof final Player player)) {
                        audience.closeDialog();
                        return;
                    }

                    final var input = response.getText("name");
                    final var name = input != null ? input.trim() : null;
                    if (name == null || name.isBlank()) {
                        final var text = Component.text("Name cannot be empty", NamedTextColor.RED);
                        audience.showDialog(createNewHologram("", text));
                        return;
                    }

                    try {
                        final var hologram = HologramProvider.instance().spawnHologram(name, player.getLocation(), ignored -> {
                        });
                        audience.showDialog(editHologram(hologram));
                    } catch (final IllegalStateException ignored) {
                        final var text = Component.text("A hologram with this name already exists", NamedTextColor.RED);
                        audience.showDialog(createNewHologram(name, text));
                    }
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Create a new hologram"))
                        .body(body)
                        .inputs(List.of(
                                DialogInput.text("name", Component.text("Hologram name")).initial(initial).build()
                        )).build())
                .type(DialogType.multiAction(List.of(create)).exitAction(back).build()));
    }

    private static DialogLike editHologram(final Hologram hologram) {
        return null; // todo: add hologram edit dialog
    }
}
