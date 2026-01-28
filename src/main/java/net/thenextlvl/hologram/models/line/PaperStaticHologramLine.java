package net.thenextlvl.hologram.models.line;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.line.StaticHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@NullMarked
public abstract class PaperStaticHologramLine<E extends Entity> extends PaperHologramLine implements StaticHologramLine {
    private final Map<Player, E> entities = new ConcurrentHashMap<>();

    protected volatile @Nullable TextColor glowColor = null;
    protected volatile Class<E> entityClass;
    protected volatile EntityType entityType;
    protected volatile boolean glowing = false;

    @SuppressWarnings("unchecked")
    public PaperStaticHologramLine(final PaperHologram hologram, final EntityType entityType) throws IllegalArgumentException {
        super(hologram);
        Preconditions.checkArgument(entityType.getEntityClass() != null, "Entity type %s is not spawnable", entityType);
        this.entityType = entityType;
        this.entityClass = (Class<E>) entityType.getEntityClass();
    }

    @Override
    public Optional<TextColor> getGlowColor() {
        return Optional.ofNullable(glowColor);
    }

    @Override
    public StaticHologramLine setGlowColor(@Nullable final TextColor color) {
        if (Objects.equals(this.glowColor, color)) return this;
        this.glowColor = color;
        updateGlowColor(color);
        return this;
    }

    @Override
    public boolean isGlowing() {
        return glowing;
    }

    @Override
    public StaticHologramLine setGlowing(final boolean glowing) {
        if (glowing == this.glowing) return this;
        this.glowing = glowing;
        forEachEntity(entity -> entity.setGlowing(glowing));
        return this;
    }

    protected abstract void updateGlowColor(@Nullable final TextColor color);

    @Override
    public Class<? extends Entity> getEntityClass() {
        return entityClass;
    }

    @Override
    public EntityType getEntityType() {
        return entityType;
    }

    @Override
    public Optional<Entity> getEntity(final Player player) {
        return Optional.ofNullable(entities.get(player));
    }

    public Map<Player, E> getEntities() {
        return entities;
    }

    public void forEachEntity(final Consumer<E> consumer) {
        entities.values().forEach(consumer);
    }

    @Override
    public <T> Optional<T> getEntity(final Player player, final Class<T> type) {
        return getEntity(player).filter(type::isInstance).map(type::cast);
    }

    protected final void updateTeamOptions(final Player player, final Entity entity) {
        if (HologramPlugin.RUNNING_FOLIA) return;
        final var team = getSettingsTeam(player, entity);
        team.color(getGlowColor().map(NamedTextColor::nearestTo).orElse(null));
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
    }

    private Team getSettingsTeam(final Player player, final Entity entity) {
        var settings = player.getScoreboard().getTeam(entity.getScoreboardEntryName());
        if (settings != null) return settings;
        settings = player.getScoreboard().registerNewTeam(entity.getScoreboardEntryName());
        settings.addEntry(entity.getScoreboardEntryName());
        return settings;
    }

    @Override
    public CompletableFuture<Void> despawn() {
        final var futures = entities.values().stream()
                .map(e -> getHologram().getPlugin().supply(e, e::remove))
                .toArray(CompletableFuture[]::new);
        entities.clear();
        return CompletableFuture.allOf(futures);
    }

    @Override
    public CompletableFuture<@Nullable Void> despawn(final Player player) {
        final var entity = entities.remove(player);
        if (entity != null) return getHologram().getPlugin().supply(entity, entity::remove);
        return CompletableFuture.completedFuture(null);
    }

    @SuppressWarnings("unchecked")
    public boolean adoptEntity(final PaperStaticHologramLine<?> oldPage, final Player player, final double offset) {
        final var entity = oldPage.entities.get(player);
        if (!entityClass.isInstance(entity)) return false;
        oldPage.entities.remove(player);
        entities.put(player, (E) entity);
        getHologram().getPlugin().supply(entity, () -> {
            entity.teleportAsync(mutateSpawnLocation(getHologram().getLocation().add(0, offset, 0)));
            preSpawn((E) entity, player);
        });
        return true;
    }

    @Override
    public CompletableFuture<@Nullable Entity> spawn(final Player player, final double offset) {
        final var existing = entities.get(player);
        final var location = mutateSpawnLocation(getHologram().getLocation().add(0, offset, 0));
        
        if (existing != null && existing.isValid()) {
            return getHologram().getPlugin().supply(existing, () -> {
                existing.teleportAsync(location);
                this.preSpawn(existing, player);
                return existing;
            });
        }

        return getHologram().getPlugin().supply(location, () -> {
            final var spawn = location.getWorld().spawn(location, entityClass, false, e -> this.preSpawn(e, player));
            getHologram().getPlugin().supply(player, () -> player.showEntity(getHologram().getPlugin(), spawn));
            entities.put(player, spawn);
            return spawn;
        });
    }

    protected Location mutateSpawnLocation(final Location location) {
        return location;
    }

    @Override
    public CompletableFuture<Void> teleportRelative(final Location previous, final Location location) {
        return CompletableFuture.allOf(getEntities().values().stream()
                .filter(Entity::isValid)
                .map(entity -> entity.teleportAsync(new Location(
                        location.getWorld(),
                        location.getX() + entity.getX() - previous.getX(),
                        location.getY() + entity.getY() - previous.getY(),
                        location.getZ() + entity.getZ() - previous.getZ(),
                        location.getYaw(), location.getPitch()
                ))).toArray(CompletableFuture[]::new));
    }

    protected void preSpawn(final E entity, final Player player) {
        updateTeamOptions(player, entity);

        entity.setPersistent(false);
        entity.setVisibleByDefault(false);

        entity.setGlowing(glowing);
        updateGlowColor(glowColor);
    }

    private @Nullable Player remove(final Entity entity) {
        final var iterator = entities.entrySet().iterator();
        while (iterator.hasNext()) {
            final var entry = iterator.next();

            if (entry.getValue().equals(entity)) {
                iterator.remove();
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public void invalidate(final Entity entity) {
        final var owner = remove(entity);
        if (owner == null) return;

        final var team = owner.getScoreboard().getTeam(entity.getScoreboardEntryName());
        if (team != null) team.unregister();
    }

    @Override
    public boolean isPart(final Entity entity) {
        return entityClass.isInstance(entity) && entities.containsValue(entityClass.cast(entity));
    }
}
