package net.thenextlvl.hologram.models.line;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.StaticHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

@NullMarked
public abstract class PaperStaticHologramLine<E extends Entity> extends PaperHologramLine implements StaticHologramLine {
    protected final Map<UUID, E> entities = new ConcurrentHashMap<>();
    protected final Map<UUID, Interaction> interactions = new ConcurrentHashMap<>();

    protected volatile @Nullable PagedHologramLine parentLine;
    protected volatile @Nullable TextColor glowColor = null;
    protected volatile Class<E> entityClass;
    protected volatile Display.Billboard billboard = Display.Billboard.CENTER;
    protected volatile EntityType entityType;
    protected volatile boolean glowing = false;

    @SuppressWarnings("unchecked")
    public PaperStaticHologramLine(final PaperHologram hologram, @Nullable final PagedHologramLine parentLine, final EntityType entityType) throws IllegalArgumentException {
        super(hologram);
        Preconditions.checkArgument(entityType.getEntityClass() != null, "Entity type %s is not spawnable", entityType);
        this.entityType = entityType;
        this.entityClass = (Class<E>) entityType.getEntityClass();
        this.parentLine = parentLine;
    }

    @Override
    public Optional<TextColor> getGlowColor() {
        return Optional.ofNullable(glowColor);
    }

    @Override
    public StaticHologramLine setGlowColor(@Nullable final TextColor color) {
        return set(this.glowColor, color, () -> {
            this.glowColor = color;
            updateGlowColor(color);
        }, false);
    }

    @Override
    public boolean isGlowing() {
        return glowing;
    }

    @Override
    public StaticHologramLine setGlowing(final boolean glowing) {
        return set(this.glowing, glowing, () -> {
            this.glowing = glowing;
            forEachEntity(entity -> entity.setGlowing(glowing));
        }, false);
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
        return Optional.ofNullable(entities.get(player.getUniqueId()));
    }

    @Override
    public <T> Optional<T> getEntity(final Player player, final Class<T> type) {
        return getEntity(player).filter(type::isInstance).map(type::cast);
    }

    @Override
    public Optional<PagedHologramLine> getParentLine() {
        return Optional.ofNullable(parentLine);
    }

    @Override
    public Optional<String> getViewPermission() {
        return super.getViewPermission().or(() -> getParentLine().flatMap(HologramLine::getViewPermission));
    }

    @Override
    public Display.Billboard getBillboard() {
        return billboard;
    }

    @Override
    public CompletableFuture<Void> despawn() {
        final var futures = Stream.concat(entities.values().stream(), interactions.values().stream())
                .map(e -> getHologram().getPlugin().supply(e, e::remove))
                .toArray(CompletableFuture[]::new);
        entities.clear();
        interactions.clear();
        return CompletableFuture.allOf(futures);
    }

    @Override
    public CompletableFuture<@Nullable Void> despawn(final UUID player) {
        final var futures = new ArrayList<CompletableFuture<Void>>(2);
        final var entity = entities.remove(player);
        if (entity != null) futures.add(getHologram().getPlugin().supply(entity, entity::remove));
        final var interaction = interactions.remove(player);
        if (interaction != null) futures.add(getHologram().getPlugin().supply(interaction, interaction::remove));
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    @Override
    public CompletableFuture<@Nullable Entity> spawn(final Player player, final double offset) {
        if (!getHologram().getWorld().equals(player.getWorld()) || !canSee(player))
            return despawn(player.getUniqueId()).thenApply(v -> null);

        return spawnEntity(player, offset).thenCompose(entity -> {
            return spawnInteraction(player, entity).thenCompose(interaction -> {
                return getHologram().getPlugin().supply(player, () -> {
                    if (player.getServer().isOwnedByCurrentRegion(entity))
                        player.showEntity(getHologram().getPlugin(), entity);
                    if (interaction != null && player.getServer().isOwnedByCurrentRegion(interaction))
                        player.showEntity(getHologram().getPlugin(), interaction);
                }).thenApply(v -> entity);
            });
        });
    }

    @SuppressWarnings("NullableProblems")
    public CompletableFuture<E> spawnEntity(final Player player, final double offset) {
        final var existing = entities.get(player.getUniqueId());
        final var location = mutateSpawnLocation(getHologram().getLocation().add(0, offset, 0));

        if (existing != null) return existing.teleportAsync(location).thenApply(ignored -> {
            this.preSpawn(existing, player);
            return existing;
        });
        return getHologram().getPlugin().supply(location, () -> {
            final var spawn = location.getWorld().spawn(location, entityClass, false, e -> this.preSpawn(e, player));
            entities.put(player.getUniqueId(), spawn);
            return spawn;
        });
    }

    public CompletableFuture<@Nullable Interaction> spawnInteraction(final Player player, final E entity) {
        final var existingInteraction = interactions.get(player.getUniqueId());
        final var location = entity.getLocation().subtract(0, getOffsetBefore(player), 0);
        if (hasActions() || getParentLine().map(HologramLine::hasActions).orElse(false)) {
            if (existingInteraction != null) return existingInteraction.teleportAsync(location).thenApply(ignored -> {
                this.preSpawnInteraction(existingInteraction, player, entity);
                return existingInteraction;
            });
            return getHologram().getPlugin().supply(location, () -> {
                final var interaction = location.getWorld().spawn(location, Interaction.class, false, e -> this.preSpawnInteraction(e, player, entity));
                interactions.put(player.getUniqueId(), interaction);
                return interaction;
            });
        } else if (existingInteraction != null) {
            return getHologram().getPlugin().supply(existingInteraction, existingInteraction::remove).thenApply(v -> null);
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

    protected Location mutateSpawnLocation(final Location location) {
        return location;
    }

    protected void preSpawnInteraction(final Interaction interaction, final Player player, final E entity) {
        interaction.setInteractionHeight((float) getHeight(player));
        interaction.setInteractionWidth((float) Math.max(1, entity.getWidth()));
        interaction.setResponsive(true);
        interaction.setPersistent(false);
        interaction.setVisibleByDefault(false);
    }

    protected void preSpawn(final E entity, final Player player) {
        updateTeamOptions(player, entity);

        entity.setPersistent(false);
        entity.setVisibleByDefault(false);

        entity.setGlowing(glowing);
        updateGlowColor(glowColor);
    }

    @Override
    public CompletableFuture<Void> teleportRelative(final Location previous, final Location location) {
        final var entities = Stream.concat(this.entities.values().stream(), interactions.values().stream());
        return CompletableFuture.allOf(entities.filter(Entity::isValid)
                .map(entity -> entity.teleportAsync(new Location(
                        location.getWorld(),
                        location.getX() + entity.getX() - previous.getX(),
                        location.getY() + entity.getY() - previous.getY(),
                        location.getZ() + entity.getZ() - previous.getZ(),
                        location.getYaw(), location.getPitch()
                ))).toArray(CompletableFuture[]::new));
    }

    @Override
    public boolean isPart(final Entity entity) {
        return (entityClass.isInstance(entity) && entities.containsValue(entityClass.cast(entity)))
                || entity instanceof final Interaction interaction && interactions.containsValue(interaction);
    }

    @Override
    public void invalidate(final Entity entity) {
        final var owner = remove(entity);
        if (owner == null) return;

        final var team = owner.getScoreboard().getTeam(entity.getScoreboardEntryName());
        if (team != null) team.unregister();
    }

    private @Nullable Player remove(final Entity entity) {
        final var iterator = entities.entrySet().iterator();
        while (iterator.hasNext()) {
            final var entry = iterator.next();

            if (entry.getValue().equals(entity)) {
                iterator.remove();
                return getHologram().getPlugin().getServer().getPlayer(entry.getKey());
            }
        }
        return null;
    }

    public CompletableFuture<@Nullable Void> adoptEntities(final PaperStaticHologramLine<?> oldPage, final Player player) {
        return adoptInteraction(oldPage, player).thenCompose(ignored -> {
            return adoptEntity(oldPage, player);
        });
    }

    private CompletableFuture<@Nullable Void> adoptInteraction(final PaperStaticHologramLine<?> oldPage, final Player player) {
        final var interaction = oldPage.interactions.remove(player.getUniqueId());
        if (interaction != null && !hasActions() && !getParentLine().map(HologramLine::hasActions).orElse(false))
            return getHologram().getPlugin().supply(interaction, interaction::remove);
        if (interaction != null) interactions.put(player.getUniqueId(), interaction);
        return CompletableFuture.completedFuture(null);
    }

    @SuppressWarnings("unchecked")
    private CompletableFuture<@Nullable Void> adoptEntity(final PaperStaticHologramLine<?> oldPage, final Player player) {
        final var entity = oldPage.entities.remove(player.getUniqueId());
        if (entity == null) return CompletableFuture.completedFuture(null);
        if (!entityClass.isInstance(entity)) return getHologram().getPlugin().supply(entity, entity::remove);
        entities.put(player.getUniqueId(), (E) entity);
        return CompletableFuture.completedFuture(null);
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

    public void forEachEntity(final Consumer<E> consumer) {
        entities.values().forEach(consumer);
    }
}
