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

    static DialogLike create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<InetSocketAddress> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, DialogLike> reopen
    ) {
        final var current = action.getInput();
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
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
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen)))))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Edit the host and port")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Transfer"))
                        .body(body)
                        .inputs(List.of(
                                DialogInput.text("host", Component.text("Host")).initial(current.getHostString()).build(),
                                DialogInput.text("port", Component.text("Port")).initial(Integer.toString(current.getPort())).build()
                        ))
                        .build())
                .type(DialogType.multiAction(List.of(save)).exitAction(back).build()));
    }
}
