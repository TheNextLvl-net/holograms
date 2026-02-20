package net.thenextlvl.hologram.line;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Represents a line type within a hologram that displays an entity.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface EntityHologramLine extends StaticHologramLine {
    /**
     * Sets the entity type of the hologram line.
     *
     * @param entityType new entity type
     * @return {@code true} if the entity type was successfully set, {@code false} otherwise
     * @throws IllegalArgumentException if the entity type is not valid
     * @since 1.0.0
     */
    @Contract(mutates = "this")
    boolean setEntityType(EntityType entityType) throws IllegalArgumentException;

    /**
     * Sets the entity type of the hologram line.
     *
     * @param entityType new entity type
     * @return {@code true} if the entity type was successfully set, {@code false} otherwise
     * @throws IllegalArgumentException if the entity type is not valid
     * @since 1.0.0
     */
    @Contract(mutates = "this")
    boolean setEntityType(Class<? extends Entity> entityType) throws IllegalArgumentException;

    /**
     * Gets the scale of the entity.
     *
     * @return scale
     * @since 0.1.0
     */
    @Contract(pure = true)
    double getScale();

    /**
     * Sets the scale of the entity.
     *
     * @param scale new scale
     * @return {@code true} if the scale was successfully set, {@code false} otherwise
     * @since 1.0.0
     */
    @Contract(mutates = "this")
    boolean setScale(double scale);
}
