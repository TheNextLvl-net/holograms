package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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

    static DialogLike create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<?> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, DialogLike> reopen
    ) {
        final var actions = new ArrayList<ActionButton>();
        actions.add(DialogSupport.actionInputButton(hologram, line, actionName, action, header, note, reopen));
        actions.add(DialogSupport.clickTypesButton(hologram, line, actionName, action, header, note, reopen));
        actions.add(ActionButton.builder(Component.text("Chance: " + action.getChance() + "%"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> EditActionChanceDialog.create(hologram, line, actionName, action, header, note, reopen));
                })))
                .build());
        actions.add(ActionButton.builder(Component.text("Cooldown: " + DialogSupport.formatIntervalInput(action.getCooldown())))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> EditActionCooldownDialog.create(hologram, line, actionName, action, header, note, reopen));
                })))
                .build());
        actions.add(ActionButton.builder(Component.text("Permission: " + action.getPermission().orElse("none")))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> EditActionPermissionDialog.create(hologram, line, actionName, action, header, note, reopen));
                })))
                .build());
        actions.add(ActionButton.builder(Component.text("Cost: " + action.getCost() + (action.getCurrency().map(currency -> " " + currency).orElse(""))))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> EditActionCostDialog.create(hologram, line, actionName, action, header, note, reopen));
                })))
                .build());
        actions.add(ActionButton.builder(Component.text("Delete", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> DeleteActionDialog.create(hologram, line, actionName, header, note, reopen));
                })))
                .build());

        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(header));
        body.add(DialogBody.plainMessage(Component.text("Type: " + DialogSupport.friendlyName(action.getActionType().name()))));
        body.add(DialogBody.plainMessage(Component.text("Input: " + DialogSupport.actionInputSummary(action))));
        body.add(DialogBody.plainMessage(Component.text("Click types: " + DialogSupport.clickTypesSummary(action.getClickTypes()))));
        if (note != null) body.add(DialogBody.plainMessage(note));

        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, ignored -> ClickActionsDialog.create(hologram, line, header, note, reopen)))))
                .build();
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(actionName))
                        .body(body)
                        .build())
                .type(DialogType.multiAction(DialogSupport.addBack(actions, back)).build()));
    }
}
