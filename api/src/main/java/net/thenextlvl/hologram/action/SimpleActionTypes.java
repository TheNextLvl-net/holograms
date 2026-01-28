package net.thenextlvl.hologram.action;

import com.google.common.io.ByteStreams;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetSocketAddress;

import static org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.PLUGIN;

final class SimpleActionTypes implements ActionTypes {
    public static final ActionTypes INSTANCE = new SimpleActionTypes();

    private final ActionType<String> sendActionbar = ActionType.create("send_actionbar", String.class, (player, actionbar) -> {
        var placeholder = Placeholder.parsed("player", player.getName());
        var message = MiniMessage.miniMessage().deserialize(actionbar, placeholder);
        player.sendActionBar(message);
    });

    private final ActionType<String> sendMessage = ActionType.create("send_message", String.class, (player, message) -> {
        var placeholder = Placeholder.parsed("player", player.getName());
        player.sendMessage(MiniMessage.miniMessage().deserialize(message, placeholder));
    });

    private final ActionType<InetSocketAddress> transfer = ActionType.create("transfer", InetSocketAddress.class, (player, address) -> {
        player.transfer(address.getHostName(), address.getPort());
    });

    private final ActionType<Location> teleport = ActionType.create("teleport", Location.class, (player, location) -> {
        player.teleportAsync(location, PLUGIN);
    });

    private final ActionType<Sound> playSound = ActionType.create("play_sound", Sound.class, Audience::playSound);

    private final ActionType<String> runConsoleCommand = ActionType.create("run_console_command", String.class, (player, input) -> {
        var command = input.replace("<player>", player.getName());
        player.getServer().dispatchCommand(player.getServer().getConsoleSender(), command);
    });

    private final ActionType<String> runCommand = ActionType.create("run_command", String.class, (player, command) -> {
        player.performCommand(command.replace("<player>", player.getName()));
    });

    private final ActionType<Title> sendTitle = ActionType.create("send_title", Title.class, Audience::showTitle);

    private final ActionType<String> connect = ActionType.create("connect", String.class, (player, server) -> {
        var plugin = JavaPlugin.getProvidingPlugin(SimpleActionTypes.class);
        var dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeUTF("Connect");
        dataOutput.writeUTF(server);
        player.sendPluginMessage(plugin, "BungeeCord", dataOutput.toByteArray());
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
    public ActionType<Title> sendTitle() {
        return sendTitle;
    }

    @Override
    public ActionType<String> connect() {
        return connect;
    }

    private boolean isDeprecated(final Enum<?> anEnum) {
        try {
            return anEnum.getDeclaringClass().getField(anEnum.name()).isAnnotationPresent(Deprecated.class);
        } catch (final NoSuchFieldException e) {
            return false;
        }
    }
}
