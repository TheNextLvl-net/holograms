package net.thenextlvl.hologram.models.line;

import com.destroystokyo.paper.entity.Pathfinder;
import net.kyori.adventure.util.TriState;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Location;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.EquipmentSlot;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PaperEntityHologramLine<E extends Entity> extends PaperHologramLine<E> implements EntityHologramLine<E> {
    private final Vector3f offset = new Vector3f();
    private double scale = 1;

    public PaperEntityHologramLine(PaperHologram hologram, Class<E> entityClass) throws IllegalArgumentException {
        super(hologram, entityClass);
    }

    @Override
    public double getHeight() {
        return getEntity().map(Entity::getHeight).orElse(0d) * scale;
    }

    @Override
    public double getOffsetAfter() {
        return 0.1;
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
        if (this.scale == scale) return this;
        this.scale = scale;
        getEntity(Attributable.class).ifPresent(this::updateScale);
        getHologram().updateHologram();
        return this;
    }

    @Override
    public Vector3f getOffset() {
        return new Vector3f(offset);
    }

    @Override
    public EntityHologramLine<E> setOffset(Vector3f offset) {
        if (this.offset.equals(offset)) return this;
        getEntity().ifPresent(entity -> {
            var location = entity.getLocation();
            location.subtract(this.offset.x(), this.offset.y(), this.offset.z());
            location.add(offset.x(), offset.y(), offset.z());
            entity.teleportAsync(location);
        });
        this.offset.set(offset);
        return this;
    }

    @Override
    protected Location mutateSpawnLocation(Location location) {
        return location.add(getOffset().x(), getOffset().y(), getOffset().z());
    }

    @Override
    protected void preSpawn(E entity) {
        entity.setSilent(true);
        entity.setInvulnerable(true);
        entity.setGravity(false);
        entity.setNoPhysics(true);

        if (entity instanceof Mob mob) {
            getHologram().getPlugin().getServer().getMobGoals().removeAllGoals(mob);
            mob.setDespawnInPeacefulOverride(TriState.FALSE);
            mob.setAware(false);
        }

        if (entity instanceof TNTPrimed explosive) {
            explosive.setFuseTicks(Integer.MAX_VALUE);
        }

        if (entity instanceof Explosive explosive) {
            explosive.setIsIncendiary(false);
            explosive.setYield(0);
        }


        if (entity instanceof Pathfinder pathfinder) {
            pathfinder.setCanFloat(false);
            pathfinder.setCanOpenDoors(false);
            pathfinder.setCanPassDoors(false);
        }

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.setAI(false);
            livingEntity.setCollidable(false);
            livingEntity.setCanPickupItems(false);
            livingEntity.setRemoveWhenFarAway(true);
        }

        if (entity instanceof Attributable attributable) {
            updateScale(attributable);
        }

        if (entity instanceof ArmorStand armorStand) {
            armorStand.setDisabledSlots(EquipmentSlot.values());
        }

        super.preSpawn(entity);
    }

    private void updateScale(Attributable attributable) {
        var attribute = attributable.getAttribute(Attribute.SCALE);
        if (attribute != null) attribute.setBaseValue(scale);
    }
}
