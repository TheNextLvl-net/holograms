package net.thenextlvl.hologram.action;

import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.jetbrains.annotations.Contract;

import java.net.InetSocketAddress;

/**
 * Represents a collection of action types that can be performed on a hologram line.
 *
 * @since 0.6.0
 */
public sealed interface ActionTypes permits SimpleActionTypes {
    /**
     * Gets the action types.
     *
     * @return action types
     * @since 0.6.0
     */
    @Contract(pure = true)
    static ActionTypes types() {
        return SimpleActionTypes.INSTANCE;
    }

    /**
     * Gets the action type for sending an action bar.
     *
     * @return action type for sending an action bar
     * @since 0.6.0
     */
    @Contract(pure = true)
    ActionType<String> sendActionbar();

    /**
     * Gets the action type for sending a message.
     *
     * @return action type for sending a message
     * @since 0.6.0
     */
    @Contract(pure = true)
    ActionType<String> sendMessage();

    /**
     * Gets the action type for transferring a player.
     *
     * @return action type for transferring a player
     * @since 0.6.0
     */
    @Contract(pure = true)
    ActionType<InetSocketAddress> transfer();

    /**
     * Gets the action type for teleporting a player.
     *
     * @return action type for teleporting a player
     * @since 0.6.0
     */
    @Contract(pure = true)
    ActionType<Location> teleport();

    /**
     * Gets the action type for playing a sound.
     *
     * @return action type for playing a sound
     * @since 0.6.0
     */
    @Contract(pure = true)
    ActionType<Sound> playSound();

    /**
     * Gets the action type for running a console command.
     *
     * @return action type for running a console command
     * @since 0.6.0
     */
    @Contract(pure = true)
    ActionType<String> runConsoleCommand();

    /**
     * Gets the action type for running a command as the player.
     *
     * @return action type for running a command as the player
     * @since 0.6.0
     */
    @Contract(pure = true)
    ActionType<String> runCommand();

    /**
     * Gets the action type for sending a title.
     *
     * @return action type for sending a title
     * @since 0.6.0
     */
    @Contract(pure = true)
    ActionType<UnparsedTitle> sendTitle();

    /**
     * Gets the action type for connecting to a server.
     *
     * @return action type for connecting to a server
     * @since 0.6.0
     */
    @Contract(pure = true)
    ActionType<String> connect();

    /**
     * Gets the action type for cycling through the pages of a hologram.
     *
     * @return action type for cycling through the pages of a hologram
     * @since 0.8.0
     */
    @Contract(pure = true)
    ActionType<Integer> cyclePage();

    /**
     * Gets the action type for setting the page of a hologram.
     *
     * @return action type for setting the page of a hologram
     * @since 0.8.0
     */
    @Contract(pure = true)
    ActionType<Integer> setPage();
}
