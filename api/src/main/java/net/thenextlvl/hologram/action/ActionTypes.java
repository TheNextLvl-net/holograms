package net.thenextlvl.hologram.action;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.jetbrains.annotations.Contract;

import java.net.InetSocketAddress;

/**
 *
 * @since 0.6.0
 */
public sealed interface ActionTypes permits SimpleActionTypes {
    @Contract(pure = true)
    static ActionTypes types() {
        return SimpleActionTypes.INSTANCE;
    }

    @Contract(pure = true)
    ActionType<String> sendActionbar();

    @Contract(pure = true)
    ActionType<String> sendMessage();

    @Contract(pure = true)
    ActionType<InetSocketAddress> transfer();

    @Contract(pure = true)
    ActionType<Location> teleport();

    @Contract(pure = true)
    ActionType<Sound> playSound();

    @Contract(pure = true)
    ActionType<String> runConsoleCommand();

    @Contract(pure = true)
    ActionType<String> runCommand();

    @Contract(pure = true)
    ActionType<Title> sendTitle();

    @Contract(pure = true)
    ActionType<String> connect();
    
    @Contract(pure = true)
    ActionType<Integer> cyclePage();
    
    @Contract(pure = true)
    ActionType<Integer> setPage();
}
