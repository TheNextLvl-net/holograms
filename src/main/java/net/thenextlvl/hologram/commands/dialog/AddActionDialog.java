package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.action.ActionType;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.models.ClickTypes;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Function;

@NullMarked
final class AddActionDialog {
    private AddActionDialog() {
    }

    static <T> DialogLike create(
            final Hologram hologram,
            final HologramLine line,
            final ActionType<T> type,
            final T input,
            final Component header,
            @Nullable final Component note,
            final String nameInitial,
            final Function<Audience, DialogLike> reopen
    ) {
        final var save = ActionButton.builder(Component.text("Create", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var actionName = response.getText("name");
                    final var name = actionName != null ? actionName.trim() : null;
                    if (name == null || name.isBlank()) {
                        DialogSupport.show(audience, ignored -> AddActionDialog.create(hologram, line, type, input, header, Component.text("Action name cannot be empty", NamedTextColor.RED), nameInitial, reopen));
                        return;
                    }

                    final var action = ClickAction.factory().create(type, EnumSet.copyOf(ClickTypes.ANY_CLICK.getClickTypes()), input);
                    if (!line.addAction(name, action)) {
                        DialogSupport.show(audience, ignored -> AddActionDialog.create(hologram, line, type, input, header, Component.text("An action with this name already exists", NamedTextColor.RED), name, reopen));
                        return;
                    }

                    DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, name, action, header, note, reopen));
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, ignored -> SelectActionTypeDialog.create(hologram, line, header, note, reopen)))))
                .build();

        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Create a " + DialogSupport.friendlyName(type.name()) + " action")));
        body.add(DialogBody.plainMessage(Component.text("You can edit its modifiers after creating it")));
        if (note != null) body.add(DialogBody.plainMessage(note));

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Add Action"))
                        .body(body)
                        .inputs(List.of(DialogInput.text("name", Component.text("Action name")).initial(nameInitial).build()))
                        .build())
                .type(DialogType.multiAction(List.of(save)).exitAction(back).build()));
    }
}
