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
     * @return this
     * @throws IllegalArgumentException if the entity type is not valid
     * @since 0.6.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    EntityHologramLine setEntityType(EntityType entityType) throws IllegalArgumentException;

    /**
     * Sets the entity type of the hologram line.
     *
     * @param entityType new entity type
     * @return this
     * @throws IllegalArgumentException if the entity type is not valid
     * @since 0.6.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    EntityHologramLine setEntityType(Class<Entity> entityType) throws IllegalArgumentException;

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
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    EntityHologramLine setScale(double scale);
}
