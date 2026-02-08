package net.thenextlvl.hologram.action;

import com.google.common.io.ByteStreams;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetSocketAddress;

import static org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.PLUGIN;

final class SimpleActionTypes implements ActionTypes {
    public static final ActionTypes INSTANCE = new SimpleActionTypes();
    private static final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(SimpleActionTypes.class);

    private final ActionType<String> sendActionbar = ActionType.create("send_actionbar", String.class, (line, player, actionbar) -> {
        var placeholder = Placeholder.parsed("player", player.getName());
        var message = MiniMessage.miniMessage().deserialize(actionbar, placeholder);
        player.sendActionBar(message);
    });

    private final ActionType<String> sendMessage = ActionType.create("send_message", String.class, (line, player, message) -> {
        var placeholder = Placeholder.parsed("player", player.getName());
        player.sendMessage(MiniMessage.miniMessage().deserialize(message, placeholder));
    });

    private final ActionType<InetSocketAddress> transfer = ActionType.create("transfer", InetSocketAddress.class, (line, player, address) -> {
        player.transfer(address.getHostName(), address.getPort());
    });

    private final ActionType<Location> teleport = ActionType.create("teleport", Location.class, (line, player, location) -> {
        player.teleportAsync(location, PLUGIN);
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
        var miniMessage = MiniMessage.miniMessage();
        var titleComponent = miniMessage.deserialize(title.title());
        var subtitleComponent = miniMessage.deserialize(title.subtitle());
        player.showTitle(Title.title(titleComponent, subtitleComponent, title.times()));
    });

    private final ActionType<String> connect = ActionType.create("connect", String.class, (line, player, server) -> {
        var dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeUTF("Connect");
        dataOutput.writeUTF(server);
        player.sendPluginMessage(plugin, "BungeeCord", dataOutput.toByteArray());
    });

    private final ActionType<Integer> cyclePage = ActionType.create("cycle_page", Integer.class, (line, player, amount) -> {
        if (line instanceof PagedHologramLine pagedLine) pagedLine.cyclePage(player, amount);
    });

    private final ActionType<Integer> setPage = ActionType.create("set_page", Integer.class, (line, player, page) -> {
        if (!(line instanceof PagedHologramLine pagedLine)) return;
        if (page >= 0 && page < pagedLine.getPageCount()) pagedLine.setPage(player, page);
        else plugin.getComponentLogger().warn("Invalid page index for hologram {}: {}/{}",
                line.getHologram().getName(), page, pagedLine.getPageCount());
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
    public ActionType<Integer> cyclePage() {
        return cyclePage;
    }

    @Override
    public ActionType<Integer> setPage() {
        return setPage;
    }
}
