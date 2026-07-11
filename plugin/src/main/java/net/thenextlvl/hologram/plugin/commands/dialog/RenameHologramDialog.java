package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
final class RenameHologramDialog {
    private RenameHologramDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(final Hologram hologram, final String initial, @Nullable final Component note) {
        final var back = BackButton.create(current -> EditHologramDialog.create(hologram, current)).width(150);

        final var body = new ArrayList<DialogBody>();
        body.add(Body.text(Component.text("Enter the new hologram name")));
        if (note != null) body.add(Body.text(note));

        final var rename = Button.callback((response, audience) -> {
            final var input = response.getText("name");
            final var name = input != null ? input.trim() : null;
            if (name == null || name.isBlank()) {
                final var text = Component.text("Name cannot be empty", NamedTextColor.RED);
                DialogSupport.show(audience, ignored -> RenameHologramDialog.create(hologram, "", text));
                return;
            }

            if (hologram.getName().equals(name)) {
                DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                return;
            }

            if (!hologram.setName(name)) {
                final var text = Component.text("A hologram with this name already exists", NamedTextColor.RED);
                DialogSupport.show(audience, ignored -> RenameHologramDialog.create(hologram, name, text));
                return;
            }

            DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
        }, Component.text("Rename")).uses(1);

        final var dialog = Dialog.multiAction().title(Component.text("Rename Hologram"));
        body.forEach(dialog::addBody);
        List.of(
                Input.text("name", Component.text("Hologram name")).initial(initial).build()
        ).forEach(dialog::addInput);
        List.of(rename).forEach(dialog::addButton);
        dialog.exitAction(back);
        return dialog;
    }
}
