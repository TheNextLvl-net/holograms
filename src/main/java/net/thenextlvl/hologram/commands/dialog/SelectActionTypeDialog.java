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
import net.kyori.adventure.text.event.ClickEvent;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.HologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;

@NullMarked
final class SelectActionTypeDialog {
    private SelectActionTypeDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final HologramLine line,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, DialogLike> reopen
    ) {
        final var actions = new ArrayList<ActionButton>();
        actions.add(DialogSupport.actionTypeButton("Send Actionbar", DialogSupport.ACTION_TYPES.sendActionbar(), hologram, line, header, note, reopen));
        actions.add(DialogSupport.actionTypeButton("Send Message", DialogSupport.ACTION_TYPES.sendMessage(), hologram, line, header, note, reopen));
        actions.add(DialogSupport.actionTypeButton("Transfer", DialogSupport.ACTION_TYPES.transfer(), hologram, line, header, note, reopen));
        actions.add(DialogSupport.actionTypeButton("Teleport", DialogSupport.ACTION_TYPES.teleport(), hologram, line, header, note, reopen));
        actions.add(DialogSupport.actionTypeButton("Play Sound", DialogSupport.ACTION_TYPES.playSound(), hologram, line, header, note, reopen));
        actions.add(DialogSupport.actionTypeButton("Run Console Command", DialogSupport.ACTION_TYPES.runConsoleCommand(), hologram, line, header, note, reopen));
        actions.add(DialogSupport.actionTypeButton("Run Command", DialogSupport.ACTION_TYPES.runCommand(), hologram, line, header, note, reopen));
        actions.add(DialogSupport.actionTypeButton("Send Title", DialogSupport.ACTION_TYPES.sendTitle(), hologram, line, header, note, reopen));
        actions.add(DialogSupport.actionTypeButton("Connect", DialogSupport.ACTION_TYPES.connect(), hologram, line, header, note, reopen));
        actions.add(DialogSupport.actionTypeButton("Cycle Page", DialogSupport.ACTION_TYPES.cyclePage(), hologram, line, header, note, reopen));
        actions.add(DialogSupport.actionTypeButton("Set Page", DialogSupport.ACTION_TYPES.setPage(), hologram, line, header, note, reopen));

        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(header));
        body.add(DialogBody.plainMessage(Component.text("Choose the action type to add")));
        if (note != null) body.add(DialogBody.plainMessage(note));

        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, ignored -> ClickActionsDialog.create(hologram, line, header, note, reopen)))))
                .build();
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Add Action"))
                        .body(body)
                        .build())
                .type(DialogType.multiAction(DialogSupport.addBack(actions, back)).build()));
    }
}
