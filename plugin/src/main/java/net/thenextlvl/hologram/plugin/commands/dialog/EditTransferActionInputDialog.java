package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.line.HologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@NullMarked
final class EditTransferActionInputDialog {
    private EditTransferActionInputDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<InetSocketAddress> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, net.thenextlvl.dialogs.Dialog<?>> reopen
    ) {
        final var current = action.getInput();
        final var save = Button.callback((response, audience) -> {
            final var host = response.getText("host");
            final var portInput = response.getText("port");
            if (host == null || host.isBlank()) {
                DialogSupport.show(audience, ignored -> EditTransferActionInputDialog.create(hologram, line, actionName, action, header, Component.text("Host cannot be empty", NamedTextColor.RED), reopen));
                return;
            }
            final int port;
            try {
                port = Integer.parseInt(portInput != null ? portInput.trim() : "");
            } catch (final NumberFormatException ignored) {
                DialogSupport.show(audience, ignored2 -> EditTransferActionInputDialog.create(hologram, line, actionName, action, header, Component.text("Port must be a number", NamedTextColor.RED), reopen));
                return;
            }
            if (port < 1 || port > 65535) {
                DialogSupport.show(audience, ignored -> EditTransferActionInputDialog.create(hologram, line, actionName, action, header, Component.text("Port must be between 1 and 65535", NamedTextColor.RED), reopen));
                return;
            }
            action.setInput(new InetSocketAddress(host.trim(), port));
            DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen));
        }, Component.text("Save", NamedTextColor.GREEN)).uses(1);
        final var back = BackButton.create(ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen));
        final var body = new ArrayList<DialogBody>();
        body.add(Body.text(Component.text("Edit the host and port")));
        if (note != null) body.add(Body.text(note));
        final var dialog = Dialog.multiAction().title(Component.text("Transfer"));
        body.forEach(dialog::addBody);
        List.of(
                Input.text("host", Component.text("Host")).initial(current.getHostString()).build(),
                Input.text("port", Component.text("Port")).initial(Integer.toString(current.getPort())).build()
        ).forEach(dialog::addInput);
        List.of(save).forEach(dialog::addButton);
        dialog.exitAction(back);
        return dialog;
    }
}
