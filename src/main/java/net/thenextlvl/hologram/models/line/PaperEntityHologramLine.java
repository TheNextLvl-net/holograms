package net.thenextlvl.hologram.models.line;

import net.kyori.adventure.util.TriState;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
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
    public EntityHologramLine<E> setScale(double scale) {
        this.scale = scale;
        getEntity(Attributable.class).ifPresent(this::updateScale);
        return this;
    }

    @Override
    protected void preSpawn(E entity) {
        entity.setSilent(true);
        entity.setInvulnerable(true);
        entity.setGravity(false);
        entity.setNoPhysics(true);

        if (entity instanceof Mob mob) {
            mob.setDespawnInPeacefulOverride(TriState.FALSE);
            mob.setAware(false);
        }

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.setAI(false);
            livingEntity.setCollidable(false);
            livingEntity.setCanPickupItems(false);
        }

        if (entity instanceof Attributable attributable) {
            updateScale(attributable);
        }

        super.preSpawn(entity);
    }

    private void updateScale(Attributable attributable) {
        var attribute = attributable.getAttribute(Attribute.SCALE);
        if (attribute != null) attribute.setBaseValue(scale);
    }
}
