package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.action.PageChange;
import net.thenextlvl.hologram.action.UnparsedTitle;
import net.thenextlvl.hologram.line.HologramLine;
import org.bukkit.Location;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.function.Function;

@NullMarked
final class EditActionInputDialog {
    private EditActionInputDialog() {
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
        final var type = action.getActionType();
        if (type == DialogSupport.ACTION_TYPES.sendActionbar() || type == DialogSupport.ACTION_TYPES.sendMessage() || type == DialogSupport.ACTION_TYPES.runConsoleCommand()
                || type == DialogSupport.ACTION_TYPES.runCommand() || type == DialogSupport.ACTION_TYPES.connect()) {
            @SuppressWarnings("unchecked") final var stringAction = (ClickAction<String>) action;
            final var label = type == DialogSupport.ACTION_TYPES.connect() ? "Server"
                    : type == DialogSupport.ACTION_TYPES.runCommand() || type == DialogSupport.ACTION_TYPES.runConsoleCommand() ? "Command"
                    : type == DialogSupport.ACTION_TYPES.sendActionbar() ? "Actionbar Text"
                    : "Message";
            return EditStringActionInputDialog.create(hologram, line, actionName, stringAction, label, header, note, reopen);
        }
        if (type == DialogSupport.ACTION_TYPES.transfer()) {
            @SuppressWarnings("unchecked") final var transferAction = (ClickAction<InetSocketAddress>) action;
            return EditTransferActionInputDialog.create(hologram, line, actionName, transferAction, header, note, reopen);
        }
        if (type == DialogSupport.ACTION_TYPES.teleport()) {
            @SuppressWarnings("unchecked") final var teleportAction = (ClickAction<Location>) action;
            return EditTeleportActionInputDialog.create(hologram, line, actionName, teleportAction, header, note, reopen);
        }
        if (type == DialogSupport.ACTION_TYPES.playSound()) {
            @SuppressWarnings("unchecked") final var soundAction = (ClickAction<Sound>) action;
            return EditSoundActionInputDialog.create(hologram, line, actionName, soundAction, header, note, reopen);
        }
        if (type == DialogSupport.ACTION_TYPES.sendTitle()) {
            @SuppressWarnings("unchecked") final var titleAction = (ClickAction<UnparsedTitle>) action;
            return EditTitleActionInputDialog.create(hologram, line, actionName, titleAction, header, note, reopen);
        }
        if (type == DialogSupport.ACTION_TYPES.cyclePage() || type == DialogSupport.ACTION_TYPES.setPage()) {
            @SuppressWarnings("unchecked") final var pageAction = (ClickAction<PageChange>) action;
            return EditPageChangeActionInputDialog.create(hologram, line, actionName, pageAction, header, note, reopen);
        }
        return EditActionDialog.create(hologram, line, actionName, action, header, note, reopen);
    }
}
