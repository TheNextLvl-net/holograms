package net.thenextlvl.hologram.line;

import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.hologram.Hologram;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a line within a hologram.
 *
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
     * Gets the class of the entity representing this line.
     *
     * @return entity class
     * @since 0.5.0
     */
    @Contract(pure = true)
    Class<? extends Entity> getEntityClass();

    /**
     * Gets the entity type of the entity representing this line.
     *
     * @return entity type
     * @since 0.3.0
     */
    @Contract(pure = true)
    EntityType getEntityType();

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
     * Gets whether this line is glowing.
     *
     * @return true if this line is glowing
     * @since 0.4.0
     */
    @Contract(pure = true)
    boolean isGlowing();

    /**
     * Sets whether this line is glowing.
     *
     * @param glowing true if this line should glow
     * @since 0.4.0
     */
    HologramLine setGlowing(boolean glowing);

    /**
     * Gets the glow color of this line.
     *
     * @return glow color
     * @since 0.4.0
     */
    @Contract(pure = true)
    Optional<TextColor> getGlowColor();

    /**
     * Sets the glow color of this line.
     *
     * @param color new color
     * @since 0.4.0
     */
    HologramLine setGlowColor(@Nullable TextColor color);
}
