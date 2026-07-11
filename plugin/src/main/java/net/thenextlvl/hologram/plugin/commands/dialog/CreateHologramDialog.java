package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import net.thenextlvl.hologram.HologramProvider;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class CreateHologramDialog {
    private CreateHologramDialog() {
    }

    static Dialog<?> create(final String initial, @Nullable final Component note) {
        final var dialog = Dialog.multiAction()
                .title(Component.text("Create a new hologram"))
                .addBody(Body.text(Component.text("Enter the name of the new hologram")));
        if (note != null) dialog.addBody(Body.text(note));
        return dialog.addInput(Input.text("name", Component.text("Hologram name"))
                        .initial(initial)
                        .build())
                .addButton(Button.callback((response, audience) -> {
                    if (!(audience instanceof final Player player)) {
                        audience.closeDialog();
                        return;
                    }

                    final var input = response.getText("name");
                    final var name = input != null ? input.trim() : null;
                    if (name == null || name.isBlank()) {
                        final var text = Component.text("Name cannot be empty", NamedTextColor.RED);
                        DialogSupport.show(audience, ignored -> CreateHologramDialog.create("", text));
                        return;
                    }

                    try {
                        final var hologram = HologramProvider.instance().spawnHologram(name, player.getLocation(), ignored -> {
                        });
                        DialogSupport.show(audience, viewer -> EditHologramDialog.create(hologram, viewer));
                    } catch (final IllegalStateException ignored) {
                        final var text = Component.text("A hologram with this name already exists", NamedTextColor.RED);
                        DialogSupport.show(audience, current -> CreateHologramDialog.create(name, text));
                    }
                }, Component.text("Create")))
                .exitAction(BackButton.create(ignored -> OverviewDialog.create()).width(150));
    }
}
