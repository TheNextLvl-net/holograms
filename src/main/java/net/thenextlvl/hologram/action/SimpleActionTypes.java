package net.thenextlvl.hologram.action;

import com.google.common.io.ByteStreams;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.title.Title;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.models.line.PaperTextHologramLine;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.net.InetSocketAddress;

import static org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.PLUGIN;

@NullMarked
public final class SimpleActionTypes implements ActionTypes {
    public static final ActionTypes INSTANCE = new SimpleActionTypes();
    private static final HologramPlugin plugin = JavaPlugin.getPlugin(HologramPlugin.class);

    private final ActionType<String> sendActionbar = ActionType.create("send_actionbar", String.class, (line, player, actionbar) -> {
        player.sendActionBar(PaperTextHologramLine.parse(plugin, line.getHologram(), line, actionbar, player));
    });

    private final ActionType<String> sendMessage = ActionType.create("send_message", String.class, (line, player, message) -> {
        player.sendMessage(PaperTextHologramLine.parse(plugin, line.getHologram(), line, message, player));
    });

    private final ActionType<InetSocketAddress> transfer = ActionType.create("transfer", InetSocketAddress.class, (line, player, address) -> {
        player.transfer(address.getHostName(), address.getPort());
    });

    private final ActionType<Location> teleport = ActionType.create("teleport", Location.class, (line, player, location) -> {
        if (location.isWorldLoaded()) player.teleportAsync(location, PLUGIN);
        else plugin.getComponentLogger().warn("Invalid target world for teleport for hologram {} ({})",
                line.getHologram().getName(), location);
    });

    private final ActionType<Sound> playSound = ActionType.create("play_sound", Sound.class, (line, player, input) -> {
        player.playSound(input);
    });

    private final ActionType<String> runConsoleCommand = ActionType.create("run_console_command", String.class, (line, player, input) -> {
        var command = input.replace("<player>", player.getName());
        player.getServer().dispatchCommand(player.getServer().getConsoleSender(), command);
    });

    private final ActionType<String> runCommand = ActionType.create("run_command", String.class, (line, player, command) -> {
        player.performCommand(command.replace("<player>", player.getName()));
    });

    private final ActionType<UnparsedTitle> sendTitle = ActionType.create("send_title", UnparsedTitle.class, (line, player, title) -> {
        var titleComponent = PaperTextHologramLine.parse(plugin, line.getHologram(), line, title.title(), player);
        var subtitleComponent = PaperTextHologramLine.parse(plugin, line.getHologram(), line, title.subtitle(), player);
        player.showTitle(Title.title(titleComponent, subtitleComponent, title.times()));
    });

    private final ActionType<String> connect = ActionType.create("connect", String.class, (line, player, server) -> {
        var dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeUTF("Connect");
        dataOutput.writeUTF(server);
        player.sendPluginMessage(plugin, "BungeeCord", dataOutput.toByteArray());
    });

    private final ActionType<PageChange> cyclePage = ActionType.create("cycle_page", PageChange.class, (line, player, change) -> {
        change.hologram().getHologram().ifPresentOrElse(hologram -> {
            hologram.getLine(change.line(), PagedHologramLine.class).ifPresentOrElse(pagedLine -> {
                pagedLine.cyclePage(player, change.page());
            }, () -> plugin.getComponentLogger().warn("Line {} of hologram {} is not a paged line",
                    change.line(), change.hologram().getName()));
        }, () -> plugin.getComponentLogger().warn("Hologram {} does not exist", change.hologram().getName()));
    });

    private final ActionType<PageChange> setPage = ActionType.create("set_page", PageChange.class, (line, player, change) -> {
        change.hologram().getHologram().ifPresentOrElse(hologram -> {
            hologram.getLine(change.line(), PagedHologramLine.class).ifPresentOrElse(pagedLine -> {
                var page = change.page();
                if (page >= 0 && page < pagedLine.getPageCount()) pagedLine.setPage(player, page);
                else plugin.getComponentLogger().warn("Invalid page index for hologram {}: {}/{}",
                        hologram.getName(), page, pagedLine.getPageCount());
            }, () -> plugin.getComponentLogger().warn("Line {} of hologram {} is not a paged line",
                    change.line(), change.hologram().getName()));
        }, () -> plugin.getComponentLogger().warn("Hologram {} does not exist", change.hologram().getName()));
    });

    @Override
    public ActionType<String> sendActionbar() {
        return sendActionbar;
    }

    @Override
    public ActionType<String> sendMessage() {
        return sendMessage;
    }

    @Override
    public ActionType<InetSocketAddress> transfer() {
        return transfer;
    }

    @Override
    public ActionType<Location> teleport() {
        return teleport;
    }

    @Override
    public ActionType<Sound> playSound() {
        return playSound;
    }

    @Override
    public ActionType<String> runConsoleCommand() {
        return runConsoleCommand;
    }

    @Override
    public ActionType<String> runCommand() {
        return runCommand;
    }

    @Override
    public ActionType<UnparsedTitle> sendTitle() {
        return sendTitle;
    }

    @Override
    public ActionType<String> connect() {
        return connect;
    }

    @Override
    public ActionType<PageChange> cyclePage() {
        return cyclePage;
    }

    @Override
    public ActionType<PageChange> setPage() {
        return setPage;
    }
}
