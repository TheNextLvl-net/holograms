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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
final class RenameHologramDialog {
    private RenameHologramDialog() {
    }

    static DialogLike create(final Hologram hologram, final String initial, @Nullable final Component note) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                })))
                .width(150)
                .build();

        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter the new hologram name")));
        if (note != null) body.add(DialogBody.plainMessage(note));

        final var rename = ActionButton.builder(Component.text("Rename"))
                .action(DialogAction.customClick((response, audience) -> {
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
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Rename Hologram"))
                        .body(body)
                        .inputs(List.of(
                                DialogInput.text("name", Component.text("Hologram name")).initial(initial).build()
                        )).build())
                .type(DialogType.multiAction(List.of(rename)).exitAction(back).build()));
    }
}
