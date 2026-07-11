package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.line.HologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;

@NullMarked
final class EditActionDialog {
    private EditActionDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<?> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, Dialog<?>> reopen
    ) {
        final var actions = new ArrayList<Button<?>>();
        actions.add(DialogSupport.actionInputButton(hologram, line, actionName, action, header, note, reopen));
        actions.add(DialogSupport.clickTypesButton(hologram, line, actionName, action, header, note, reopen));
        actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, ignored -> EditActionChanceDialog.create(hologram, line, actionName, action, header, note, reopen));
        }), Component.text("Chance: " + action.getChance() + "%")));
        actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, ignored -> EditActionCooldownDialog.create(hologram, line, actionName, action, header, note, reopen));
        }), Component.text("Cooldown: " + DialogSupport.formatIntervalInput(action.getCooldown()))));
        actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, ignored -> EditActionPermissionDialog.create(hologram, line, actionName, action, header, note, reopen));
        }), Component.text("Permission: " + action.getPermission().orElse("none"))));
        actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, ignored -> EditActionCostDialog.create(hologram, line, actionName, action, header, note, reopen));
        }), Component.text("Cost: " + action.getCost() + (action.getCurrency().map(currency -> " " + currency).orElse("")))));
        actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, ignored -> DeleteActionDialog.create(hologram, line, actionName, header, note, reopen));
        }), Component.text("Delete", NamedTextColor.RED)));

        final var dialog = Dialog.multiAction()
                .title(Component.text(actionName))
                .addBody(Body.text(header))
                .addBody(Body.text(Component.text("Type: " + DialogSupport.friendlyName(action.getActionType().name()))))
                .addBody(Body.text(Component.text("Input: " + DialogSupport.actionInputSummary(action))))
                .addBody(Body.text(Component.text("Click types: " + DialogSupport.clickTypesSummary(action.getClickTypes()))));
        if (note != null) dialog.addBody(Body.text(note));

        actions.forEach(dialog::addButton);
        return dialog.exitAction(BackButton.create(ignored -> ClickActionsDialog.create(hologram, line, header, note, reopen)));
    }
}
