package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.DialogBase;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.MultiActionDialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.action.ActionType;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.plugin.models.ClickTypes;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Function;

@NullMarked
final class AddActionDialog {
    private AddActionDialog() {
    }

    static <T> MultiActionDialog create(
            final Hologram hologram,
            final HologramLine line,
            final ActionType<T> type,
            final T input,
            final Component header,
            @Nullable final Component note,
            final String nameInitial,
            final Function<Audience, Dialog<?>> reopen
    ) {
        final var save = Button.callback((response, audience) -> {
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
        }, Component.text("Create", NamedTextColor.GREEN)).uses(1);
        final var back = BackButton.create(ignored -> {
            return SelectActionTypeDialog.create(hologram, line, header, note, reopen);
        });

        final var dialog = Dialog.multiAction()
                .closeAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)
                .title(Component.text("Add Action"))
                .addBody(Body.text(Component.text("Create a " + DialogSupport.friendlyName(type.name()) + " action")))
                .addBody(Body.text(Component.text("You can edit its modifiers after creating it")));
        if (note != null) dialog.addBody(Body.text(note));

        return dialog
                .addInput(Input.text("name", Component.text("Action name")).initial(nameInitial))
                .exitAction(back);
    }
}
