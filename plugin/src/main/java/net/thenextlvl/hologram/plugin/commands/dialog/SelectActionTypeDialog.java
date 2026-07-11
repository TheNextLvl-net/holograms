package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
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

    static net.thenextlvl.dialogs.Dialog<?> create(
            final Hologram hologram,
            final HologramLine line,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, net.thenextlvl.dialogs.Dialog<?>> reopen
    ) {
        final var actions = new ArrayList<Button<?>>();
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
        body.add(Body.text(header));
        body.add(Body.text(Component.text("Choose the action type to add")));
        if (note != null) body.add(Body.text(note));

        final var back = BackButton.create(ignored -> ClickActionsDialog.create(hologram, line, header, note, reopen));
        final var dialog = Dialog.multiAction().title(Component.text("Add Action"));
        body.forEach(dialog::addBody);
        DialogSupport.addBack(actions, back).forEach(dialog::addButton);
        return dialog;
    }
}
