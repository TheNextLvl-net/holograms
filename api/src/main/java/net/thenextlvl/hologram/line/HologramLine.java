package net.thenextlvl.hologram.line;

import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.action.ClickAction;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Represents a line within a hologram.
 *
 * @see PagedHologramLine
 * @see StaticHologramLine
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface HologramLine {
    /**
     * Gets the hologram this line belongs to.
     *
     * @return hologram
     * @since 0.1.0
     */
    @Contract(pure = true)
    Hologram getHologram();

    /**
     * Gets the entity representing this line.
     *
     * @return entity
     * @since 0.4.0
     */
    @Contract(pure = true)
    Optional<Entity> getEntity(Player player);

    /**
     * Gets the entity representing this line, if it is of the given type.
     *
     * @param type entity type
     * @param <T>  entity type
     * @return entity
     * @since 0.4.0
     */
    @Contract(pure = true)
    <T> Optional<T> getEntity(Player player, Class<T> type);

    /**
     * Gets the type of this line.
     *
     * @return line type
     * @since 0.3.0
     */
    @Contract(pure = true)
    LineType getType();

    /**
     * Gets the world of this line.
     *
     * @return the world of this line
     * @since 0.3.0
     */
    @Contract(pure = true)
    World getWorld();

    /**
     * Checks if the given entity is part of this line.
     *
     * @param entity the entity to check
     * @return {@code true} if the entity is part of this line, {@code false} otherwise
     * @since 0.5.0
     */
    @Contract(pure = true)
    boolean isPart(Entity entity);

    /**
     * Gets the click actions for this line.
     *
     * @return click actions
     * @since 0.8.0
     */
    @Unmodifiable
    @Contract(pure = true)
    Map<String, ClickAction<?>> getActions();

    /**
     * Gets the click action for this line.
     *
     * @param name action name
     * @return click action
     * @since 0.8.0
     */
    @Contract(pure = true)
    Optional<ClickAction<?>> getAction(String name);

    /**
     * Checks if this line has the given click action.
     *
     * @param action click action
     * @return {@code true} if this line has the given click action, {@code false} otherwise
     * @since 0.8.0
     */
    @Contract(pure = true)
    boolean hasAction(ClickAction<?> action);

    /**
     * Checks if this line has a click action with the given name.
     *
     * @param name action name
     * @return {@code true} if this line has a click action with the given name, {@code false} otherwise
     * @since 0.8.0
     */
    @Contract(pure = true)
    boolean hasAction(String name);

    /**
     * Checks if this line has any click actions.
     *
     * @return {@code true} if this line has any click actions, {@code false} otherwise
     * @since 0.8.0
     */
    boolean hasActions();

    /**
     * Adds a click action for this line.
     *
     * @param name   action name
     * @param action click action
     * @return {@code true} if the action was added, {@code false} otherwise
     * @since 0.8.0
     */
    @Contract(mutates = "this")
    boolean addAction(String name, ClickAction<?> action);

    /**
     * Removes a click action for this line.
     *
     * @param name action name
     * @return {@code true} if the action was removed, {@code false} otherwise
     * @since 0.8.0
     */
    @Contract(mutates = "this")
    boolean removeAction(String name);

    /**
     * Iterates over all click actions for this line.
     *
     * @param action action consumer
     * @since 0.8.0
     */
    void forEachAction(BiConsumer<String, ? super ClickAction<?>> action);

    /**
     * Returns the view permission of this line.
     *
     * @return view permission
     * @since 0.9.0
     */
    @Contract(pure = true)
    Optional<String> getViewPermission();

    /**
     * Sets the view permission of this line.
     *
     * @param permission view permission
     * @return {@code true} if the view permission was changed, {@code false} otherwise
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    boolean setViewPermission(@Nullable String permission);

    /**
     * Checks if the given player can see this line.
     *
     * @param player player
     * @return {@code true} if the given player can see this line, {@code false} otherwise
     * @see #getViewPermission()
     * @since 0.9.0
     */
    @Contract(pure = true)
    boolean canSee(Player player);
}
