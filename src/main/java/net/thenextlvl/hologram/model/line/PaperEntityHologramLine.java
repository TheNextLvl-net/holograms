package net.thenextlvl.hologram.model.line;

import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.model.PaperHologram;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PaperEntityHologramLine<E extends Entity> extends PaperHologramLine<E> implements EntityHologramLine<E> {
    private double scale = 1;

    public PaperEntityHologramLine(PaperHologram hologram, Class<E> entityClass) throws IllegalArgumentException {
        super(hologram, entityClass);
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

    @Override
    protected void preSpawn(E entity) {
        getEntity(Attributable.class).ifPresent(attributable -> {
            var attribute = attributable.getAttribute(Attribute.SCALE);
            if (attribute != null) attribute.setBaseValue(scale);
        });
        super.preSpawn(entity);
    }
}
