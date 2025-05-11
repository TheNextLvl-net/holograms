package net.thenextlvl.hologram.model.line;

import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.LineType;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;

@NullMarked
public class PaperEntityHologramLine<E extends Entity> extends PaperHologramLine<E> implements EntityHologramLine<E> {
    private final Class<E> entityClass;
    private final EntityType entityType;

    private double scale = 1;

    public PaperEntityHologramLine(Hologram hologram, Class<E> entityClass) throws IllegalArgumentException {
        super(hologram);
        this.entityType = Arrays.stream(EntityType.values())
                .filter(type -> type.getEntityClass() != null)
                .filter(type -> type.getEntityClass().isAssignableFrom(entityClass))
                .findAny().orElseThrow(() -> new IllegalArgumentException("Entity type not found for " + entityClass));
        this.entityClass = entityClass;
    }

    @Override
    public Class<E> getTypeClass() {
        return entityClass;
    }

    @Override
    public EntityType getEntityType() {
        return entityType;
    }

    @Override
    public LineType getType() {
        return LineType.ENTITY;
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public void setScale(double scale) {
        this.scale = scale;
        getEntity(Attributable.class).ifPresent(attributable -> {
            var attribute = attributable.getAttribute(Attribute.SCALE);
            if (attribute != null) attribute.setBaseValue(scale);
        });
    }
}
