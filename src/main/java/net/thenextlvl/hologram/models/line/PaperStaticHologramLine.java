package net.thenextlvl.hologram.models.line;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.hologram.line.StaticHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public abstract class PaperStaticHologramLine<E extends Entity> extends PaperHologramLine implements StaticHologramLine {
    private final Class<E> entityClass;
    private final EntityType entityType;
    private final Map<Player, E> entities = new ConcurrentHashMap<>();

    protected volatile @Nullable TextColor glowColor = null;
    protected volatile boolean glowing = false;

    public PaperStaticHologramLine(final PaperHologram hologram, final Class<E> entityClass) {
        super(hologram);
        this.entityType = Arrays.stream(EntityType.values())
                .filter(type -> type.getEntityClass() != null)
                .filter(type -> type.getEntityClass().isAssignableFrom(entityClass))
                .findAny().orElseThrow(() -> new IllegalArgumentException("Entity type not found for " + entityClass));
        this.entityClass = entityClass;
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
        getEntities().values().forEach(entity -> entity.setGlowing(glowing));
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
        return Optional.ofNullable(getEntities().get(player));
    }

    public Map<Player, E> getEntities() {
        return entities;
    }

    @Override
    public <T> Optional<T> getEntity(final Player player, final Class<T> type) {
        return getEntity(player).filter(type::isInstance).map(type::cast);
    }

    protected final void updateTeamOptions(final Player player, final Entity entity) {
        final var team = getSettingsTeam(player, entity);
        team.color(getGlowColor().map(NamedTextColor::nearestTo).orElse(null));
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    private Team getSettingsTeam(final Player player, final Entity entity) {
        var settings = player.getScoreboard().getTeam(entity.getScoreboardEntryName());
        if (settings != null) return settings;
        settings = player.getScoreboard().registerNewTeam(entity.getScoreboardEntryName());
        settings.addEntry(entity.getScoreboardEntryName());
        return settings;
    }

    @Override
    public void despawn() {
        entities.values().removeIf(entity -> {
            entity.remove();
            return true;
        });
    }

    @Override
    public void despawn(final Player player) {
        final var remove = entities.remove(player);
        if (remove != null) remove.remove();
    }

    public @Nullable E removeEntity(final Player player) {
        return entities.remove(player);
    }

    @SuppressWarnings("unchecked")
    public boolean adoptEntity(final Player player, final Entity entity) {
        if (!entityClass.isInstance(entity)) return false;
        entities.put(player, (E) entity);
        preSpawn((E) entity, player);
        return true;
    }

    @Override
    public Entity spawn(final Player player, final double offset) {
        return entities.compute(player, (p, existing) -> {
            if (existing != null && existing.isValid()) return existing;
            final var location = mutateSpawnLocation(getHologram().getLocation().add(0, offset, 0));
            final var spawn = location.getWorld().spawn(location, entityClass, false, e -> this.preSpawn(e, player));
            player.showEntity(getHologram().getPlugin(), spawn);
            return spawn;
        });
    }

    protected Location mutateSpawnLocation(final Location location) {
        return location;
    }

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

        // if (hologram.getViewPermission() != null || !hologram.isVisibleByDefault())
        //     plugin.getServer().getOnlinePlayers().forEach(this::updateVisibility);
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

    public void invalidate(final Entity entity) {
        final var owner = remove(entity);
        if (owner == null) return;

        final var team = owner.getScoreboard().getTeam(entity.getScoreboardEntryName());
        if (team != null) team.unregister();
    }

    @Override
    public boolean isPart(final Entity entity) {
        return getEntities().containsValue(entity); // todo: fix this inspection
    }
}
