package net.thenextlvl.hologram.models.line;

import com.destroystokyo.paper.entity.Pathfinder;
import net.kyori.adventure.text.format.TextColor;
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
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.EquipmentSlot;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class PaperEntityHologramLine<E extends Entity> extends PaperHologramLine<E> implements EntityHologramLine {
    private volatile Vector3f offset = new Vector3f();
    private volatile double scale = 1;

    public PaperEntityHologramLine(final PaperHologram hologram, final Class<E> entityClass) throws IllegalArgumentException {
        super(hologram, entityClass);
    }

    @Override
    protected void updateGlowColor(@Nullable final TextColor color) {
        getEntities().forEach(this::updateTeamOptions);
    }

    @Override
    public double getHeight(final Player player) {
        return getEntity(player).map(Entity::getHeight).orElse(0d) * scale;
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
    public EntityHologramLine setScale(final double scale) {
        if (this.scale == scale) return this;
        this.scale = scale;
        getEntities().values().stream()
                .filter(Attributable.class::isInstance)
                .map(Attributable.class::cast)
                .forEach(this::updateScale);
        getHologram().updateHologram();
        return this;
    }

    @Override
    public Vector3f getOffset() {
        return new Vector3f(offset);
    }

    @Override
    public EntityHologramLine setOffset(final Vector3f newOffset) {
        final var oldOffset = this.offset;
        if (oldOffset.equals(newOffset)) return this;
        final var copy = new Vector3f(newOffset);
        this.offset = copy;
        getEntities().values().forEach(entity -> {
            final var location = entity.getLocation();
            location.subtract(oldOffset.x(), oldOffset.y(), oldOffset.z());
            location.add(copy.x(), copy.y(), copy.z());
            entity.teleportAsync(location);
        });
        return this;
    }

    @Override
    protected Location mutateSpawnLocation(final Location location) {
        return location.add(offset.x(), offset.y(), offset.z());
    }

    @Override
    protected void preSpawn(final E entity, final Player player) {
        entity.setSilent(true);
        entity.setInvulnerable(true);
        entity.setGravity(false);
        entity.setNoPhysics(true);

        if (entity instanceof final Mob mob) {
            getHologram().getPlugin().getServer().getMobGoals().removeAllGoals(mob);
            mob.setDespawnInPeacefulOverride(TriState.FALSE);
            mob.setAware(false);
        }

        if (entity instanceof final TNTPrimed explosive) {
            explosive.setFuseTicks(Integer.MAX_VALUE);
        }

        if (entity instanceof final Explosive explosive) {
            explosive.setIsIncendiary(false);
            explosive.setYield(0);
        }


        if (entity instanceof final Pathfinder pathfinder) {
            pathfinder.setCanFloat(false);
            pathfinder.setCanOpenDoors(false);
            pathfinder.setCanPassDoors(false);
        }

        if (entity instanceof final LivingEntity livingEntity) {
            livingEntity.setAI(false);
            livingEntity.setCollidable(false);
            livingEntity.setCanPickupItems(false);
            livingEntity.setRemoveWhenFarAway(true);
        }

        if (entity instanceof final Attributable attributable) {
            updateScale(attributable);
        }

        if (entity instanceof final ArmorStand armorStand) {
            armorStand.setDisabledSlots(EquipmentSlot.values());
        }

        super.preSpawn(entity, player);
    }

    private void updateScale(final Attributable attributable) {
        final var attribute = attributable.getAttribute(Attribute.SCALE);
        if (attribute != null) attribute.setBaseValue(scale);
    }
}
