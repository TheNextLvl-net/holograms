package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.HologramProvider;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class CreateHologramDialog {
    private CreateHologramDialog() {
    }

    static DialogControl create(final String initial, @Nullable final Component note) {
        return DialogControl.create("Create a new hologram")
                .body("Enter the name of the new hologram")
                .note(note)
                .input(DialogInput.text("name", Component.text("Hologram name")).initial(initial).build())
                .submit(Component.text("Create"), (response, audience) -> {
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
                })
                .back(150, ignored -> OverviewDialog.create());
    }
}
