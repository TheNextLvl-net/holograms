package net.thenextlvl.hologram.models;

import com.google.common.base.Preconditions;
import io.papermc.paper.math.Position;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.hologram.models.line.PaperBlockHologramLine;
import net.thenextlvl.hologram.models.line.PaperEntityHologramLine;
import net.thenextlvl.hologram.models.line.PaperHologramLine;
import net.thenextlvl.hologram.models.line.PaperItemHologramLine;
import net.thenextlvl.hologram.models.line.PaperPagedHologramLine;
import net.thenextlvl.hologram.models.line.PaperTextHologramLine;
import net.thenextlvl.nbt.NBTOutputStream;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagSerializable;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.ListTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static net.thenextlvl.hologram.HologramPlugin.ISSUES;

@NullMarked
public class PaperHologram implements Hologram, TagSerializable<CompoundTag> {
    private final List<HologramLine> lines = new CopyOnWriteArrayList<>();
    private final Set<UUID> viewers = new ConcurrentSkipListSet<>();
    private final Set<Player> spawned = ConcurrentHashMap.newKeySet();

    private final HologramPlugin plugin;

    private volatile String name;

    private volatile Path dataFile;
    private volatile Path backupFile;

    private volatile Location location;
    private volatile @Nullable String viewPermission;

    private volatile boolean persistent = true;
    private volatile boolean visibleByDefault = true;

    public PaperHologram(final HologramPlugin plugin, final String name, final Location location) {
        Preconditions.checkArgument(location.getWorld() != null, "World cannot be null");

        this.plugin = plugin;
        this.location = location;
        this.name = name;

        final var dataFolder = plugin.hologramProvider().getDataFolder(location.getWorld());
        this.dataFile = dataFolder.resolve(name + ".dat");
        this.backupFile = dataFolder.resolve(name + ".dat_old");
    }

    public PaperHologram(final HologramPlugin plugin, final String name, final World world) {
        this(plugin, name, new Location(world, 0, 0, 0));
    }

    public HologramPlugin getPlugin() {
        return plugin;
    }

    public Stream<? extends Entity> getEntities(final Player player) {
        return lines.stream()
                .map(hologramLine -> hologramLine.getEntity(player).orElse(null))
                .filter(Objects::nonNull);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean setName(final String name) {
        if (Objects.equals(this.name, name)) return false;
        if (plugin.hologramProvider().hasHologram(name)) return false;

        if (!updatePaths(getWorld(), name)) return false;

        this.name = name;
        updateText();
        return true;
    }

    private boolean updatePaths(final World world, final String name) {
        final var dataFolder = plugin.hologramProvider().getDataFolder(world);
        final var dataFile = dataFolder.resolve(name + ".dat");
        final var backupFile = dataFolder.resolve(name + ".dat_old");

        try {
            Files.createDirectories(dataFolder);
            if (Files.isRegularFile(getDataFile())) Files.move(getDataFile(), dataFile, REPLACE_EXISTING);
            if (Files.isRegularFile(getBackupFile())) Files.move(getBackupFile(), backupFile, REPLACE_EXISTING);
        } catch (final IOException e) {
            plugin.getComponentLogger().warn("Failed to move hologram data files for: {}", getName(), e);
            plugin.getComponentLogger().warn("Please look for similar issues or report this on GitHub: {}", ISSUES);
            HologramPlugin.ERROR_TRACKER.trackError(e);
            return false;
        }

        this.dataFile = dataFile;
        this.backupFile = backupFile;
        return true;
    }

    @Override
    public Location getLocation() {
        return location.clone();
    }

    @Override
    public World getWorld() {
        return location.getWorld();
    }

    public boolean isInChunk(final Chunk chunk) {
        final var chunkX = location.getBlockX() >> 4;
        final var chunkZ = location.getBlockZ() >> 4;
        return chunkX == chunk.getX() && chunkZ == chunk.getZ();
    }

    @Override
    public CompletableFuture<Boolean> teleportAsync(final Location location) {
        Preconditions.checkArgument(location.getWorld() != null, "World cannot be null");
        final var previous = this.location;
        final var success = setLocation(location.clone());
        if (!success) return CompletableFuture.completedFuture(false);
        return CompletableFuture.allOf(lines.stream()
                .map(line -> {
                    if (line instanceof final PaperHologramLine<?> paperLine) 
                        return paperLine.teleportRelative(previous, location);
                    else if (line instanceof final PaperPagedHologramLine pagedLine) 
                        return pagedLine.teleportRelative(previous, location);
                    return CompletableFuture.<Void>completedFuture(null);
                })
                .toArray(CompletableFuture[]::new)
        ).thenApply(v -> true);
    }

    private boolean setLocation(final Location location) {
        if (this.location.equals(location)) return false;

        if (this.location.getWorld().equals(location.getWorld())) {
            this.location = location;
            return true;
        }

        if (!updatePaths(location.getWorld(), name)) return false;

        this.location = location;
        return true;
    }

    @Override
    public Stream<HologramLine> getLines() {
        return lines.stream();
    }

    @Override
    public int getLineCount() {
        return lines.size();
    }

    @Override
    public Optional<HologramLine> getLine(final int index) {
        if (index < 0 || index >= lines.size()) return Optional.empty();
        return Optional.of(lines.get(index));
    }

    @Override
    public <T extends HologramLine> Optional<T> getLine(final int index, final Class<T> type) {
        return getLine(index).filter(type::isInstance).map(type::cast);
    }

    @Override
    public int getLineIndex(final HologramLine line) {
        return lines.indexOf(line);
    }

    @Override
    public boolean removeLine(final HologramLine line) {
        final var removed = lines.remove(line);
        if (removed) despawnLine(line);
        updateHologram();
        return removed;
    }

    @Override
    public boolean removeLine(final int index) {
        if (index < 0 || index >= lines.size()) return false;
        final var removed = lines.remove(index);
        despawnLine(removed);
        updateHologram();
        return true;
    }

    @Override
    public boolean removeLines(final Collection<HologramLine> lines) {
        final var removed = lines.stream().map(this::removeLine).reduce(false, Boolean::logicalOr);
        if (removed) updateHologram();
        return removed;
    }

    @Override
    public void clearLines() {
        if (lines.isEmpty()) return;
        lines.forEach(this::despawnLine);
        lines.clear();
        updateHologram();
    }

    private void despawnLine(final HologramLine line) {
        if (line instanceof final PaperHologramLine<?> paperLine) {
            paperLine.getEntities().forEach((player, entity) -> entity.remove());
        } else if (line instanceof final PaperPagedHologramLine pagedLine) {
            pagedLine.despawn();
        }
    }

    @Override
    public boolean hasLine(final HologramLine line) {
        return lines.contains(line);
    }

    @Override
    public boolean moveLine(final int line, final int index) {
        if (line == index) return false;
        if (line < 0 || line >= lines.size()) return false;
        if (index < 0 || index >= lines.size()) return false;
        lines.add(index, lines.remove(line));
        updateHologram();
        return true;
    }

    @Override
    public boolean swapLines(final int line1, final int line2) {
        if (line1 == line2) return false;
        if (line1 < 0 || line1 >= lines.size()) return false;
        if (line2 < 0 || line2 >= lines.size()) return false;
        final var hologramLine1 = lines.get(line1);
        final var hologramLine2 = lines.get(line2);
        lines.set(line1, hologramLine2);
        lines.set(line2, hologramLine1);
        updateHologram();
        return true;
    }

    @Override
    public EntityHologramLine addEntityLine(final EntityType entityType) throws IllegalArgumentException {
        return addEntityLine(entityType, lines.size());
    }

    @Override
    public EntityHologramLine addEntityLine(final Class<? extends Entity> entityType) throws IllegalArgumentException {
        return addEntityLine(entityType, lines.size());
    }

    @Override
    public EntityHologramLine addEntityLine(final EntityType entityType, final int index) throws IllegalArgumentException {
        Preconditions.checkArgument(entityType.getEntityClass() != null, "Cannot spawn entity of type %s", entityType);
        return addEntityLine(entityType.getEntityClass(), index);
    }

    @Override
    public EntityHologramLine addEntityLine(final Class<? extends Entity> entityType, final int index) throws IllegalArgumentException {
        final var hologramLine = new PaperEntityHologramLine<>(this, entityType);
        lines.add(index, hologramLine);
        updateHologram();
        return hologramLine;
    }

    @Override
    public BlockHologramLine addBlockLine() {
        return addBlockLine(lines.size());
    }

    @Override
    public BlockHologramLine addBlockLine(final int index) {
        final var hologramLine = new PaperBlockHologramLine(this);
        lines.add(index, hologramLine);
        updateHologram();
        return hologramLine;
    }

    @Override
    public ItemHologramLine addItemLine() {
        return addItemLine(lines.size());
    }

    @Override
    public ItemHologramLine addItemLine(final int index) {
        final var hologramLine = new PaperItemHologramLine(this);
        lines.add(index, hologramLine);
        updateHologram();
        return hologramLine;
    }

    @Override
    public TextHologramLine addTextLine() {
        return addTextLine(lines.size());
    }

    @Override
    public TextHologramLine addTextLine(final int index) {
        final var hologramLine = new PaperTextHologramLine(this);
        lines.add(index, hologramLine);
        updateHologram();
        return hologramLine;
    }

    @Override
    public EntityHologramLine setEntityLine(final EntityType entityType, final int index) throws IllegalArgumentException {
        Preconditions.checkArgument(entityType.getEntityClass() != null, "Cannot spawn entity of type %s", entityType);
        return setEntityLine(entityType.getEntityClass(), index);
    }

    @Override
    public EntityHologramLine setEntityLine(final Class<? extends Entity> entityType, final int index) throws IllegalArgumentException {
        final var hologramLine = new PaperEntityHologramLine<>(this, entityType);
        despawnLine(lines.set(index, hologramLine));
        updateHologram();
        return hologramLine;
    }

    @Override
    public BlockHologramLine setBlockLine(final int index) {
        final var hologramLine = new PaperBlockHologramLine(this);
        despawnLine(lines.set(index, hologramLine));
        updateHologram();
        return hologramLine;
    }

    @Override
    public ItemHologramLine setItemLine(final int index) {
        final var hologramLine = new PaperItemHologramLine(this);
        despawnLine(lines.set(index, hologramLine));
        updateHologram();
        return hologramLine;
    }

    @Override
    public TextHologramLine setTextLine(final int index) {
        final var hologramLine = new PaperTextHologramLine(this);
        despawnLine(lines.set(index, hologramLine));
        updateHologram();
        return hologramLine;
    }

    @Override
    public PagedHologramLine addPagedLine() {
        final var hologramLine = new PaperPagedHologramLine(this);
        lines.add(hologramLine);
        updateHologram();
        return hologramLine;
    }

    @Override
    public PagedHologramLine addPagedLine(final int index) throws IndexOutOfBoundsException {
        final var hologramLine = new PaperPagedHologramLine(this);
        lines.add(index, hologramLine);
        updateHologram();
        return hologramLine;
    }

    @Override
    public PagedHologramLine setPagedLine(final int index) throws IndexOutOfBoundsException {
        final var hologramLine = new PaperPagedHologramLine(this);
        despawnLine(lines.set(index, hologramLine));
        updateHologram();
        return hologramLine;
    }

    @Override
    public Optional<String> getViewPermission() {
        return Optional.ofNullable(viewPermission);
    }

    @Override
    public boolean setViewPermission(@Nullable final String permission) {
        if (Objects.equals(this.viewPermission, permission)) return false;
        this.viewPermission = permission;
        updateVisibility();
        return true;
    }

    @Override
    public @Unmodifiable Set<UUID> getViewers() {
        return Set.copyOf(viewers);
    }

    @Override
    public boolean addViewer(final UUID player) {
        if (!viewers.add(player)) return false;
        if (lines.isEmpty() || isVisibleByDefault()) return true;
        final var online = plugin.getServer().getPlayer(player);
        if (online != null) spawn(online);
        return true;
    }

    @Override
    public boolean addViewers(final Collection<UUID> players) {
        return players.stream().map(this::addViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean removeViewer(final UUID player) {
        if (!viewers.remove(player)) return false;
        if (lines.isEmpty() || isVisibleByDefault()) return true;
        final var online = plugin.getServer().getPlayer(player);
        if (online != null) despawn(online);
        return true;
    }

    @Override
    public boolean removeViewers(final Collection<UUID> players) {
        return players.stream().map(this::removeViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean isViewer(final UUID player) {
        return viewers.contains(player);
    }

    @Override
    public boolean canSee(final Player player) {
        if (lines.isEmpty()) return false;
        if (!player.getWorld().equals(location.getWorld())) return false;
        if (!isVisibleByDefault() && !isViewer(player.getUniqueId())) return false;
        return getViewPermission().map(player::hasPermission).orElse(true);
    }

    @Override
    public boolean isVisibleByDefault() {
        return visibleByDefault;
    }

    @Override
    public boolean setVisibleByDefault(final boolean visible) {
        if (this.visibleByDefault == visible) return false;
        this.visibleByDefault = visible;
        return true;
    }

    @Override
    public boolean setPersistent(final boolean persistent) {
        if (this.persistent == persistent) return false;
        this.persistent = persistent;
        return true;
    }

    @Override
    public boolean isPersistent() {
        return persistent;
    }

    @Override
    public boolean persist() {
        if (!isPersistent()) return false;
        final var file = getDataFile();
        final var backup = getBackupFile();
        try {
            if (Files.isRegularFile(file)) Files.move(file, backup, REPLACE_EXISTING);
            else Files.createDirectories(file.getParent());
            try (final var outputStream = NBTOutputStream.create(file)) {
                outputStream.writeTag(getName(), serialize());
                return true;
            }
        } catch (final Throwable t) {
            if (Files.isRegularFile(backup)) try {
                Files.copy(backup, file, REPLACE_EXISTING);
                plugin.getComponentLogger().warn("Recovered hologram {} from potential data loss", getName());
            } catch (final IOException e) {
                plugin.getComponentLogger().error("Failed to restore hologram {}", getName(), e);
            }
            plugin.getComponentLogger().error("Failed to save hologram {}", getName(), t);
            plugin.getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            HologramPlugin.ERROR_TRACKER.trackError(t);
            return false;
        }
    }

    @Override
    public Path getDataFile() {
        return dataFile;
    }

    @Override
    public Path getBackupFile() {
        return backupFile;
    }

    @Override
    public void spawn() {
        plugin.getServer().getOnlinePlayers().forEach(this::spawn);
    }

    @Override
    public boolean spawn(final Player player) {
        if (!canSee(player)) return false;
        if (!location.isChunkLoaded()) return false;
        if (!spawned.add(player)) return false;

        var offset = 0d;
        // Start from the bottom line, going up
        for (var index = lines.size() - 1; index >= 0; index--) {
            final var line = lines.get(index);
            if (line instanceof final PaperHologramLine<?> hologramLine) {
                hologramLine.spawn(player, offset + hologramLine.getOffsetBefore(player));
                offset += 0.05 + hologramLine.getHeight(player) + hologramLine.getOffsetAfter();
            } else if (line instanceof final PaperPagedHologramLine pagedLine) {
                pagedLine.spawn(player, offset + pagedLine.getOffsetBefore(player));
                offset += 0.05 + pagedLine.getHeight(player) + pagedLine.getOffsetAfter();
            }
        }
        return true;
    }

    @Override
    public void despawn() {
        spawned.forEach(this::despawn);
    }

    @Override
    public boolean despawn(final Player player) {
        if (!spawned.remove(player)) return false;
        lines.forEach(hologramLine -> {
            if (hologramLine instanceof final PaperHologramLine<?> paperLine) paperLine.despawn(player);
            else if (hologramLine instanceof final PaperPagedHologramLine pagedLine) pagedLine.despawn(player);
        });
        return true;
    }

    @Override
    public boolean isSpawned(final Player player) {
        return spawned.contains(player);
    }

    @Override
    public Iterator<HologramLine> iterator() {
        return lines.iterator();
    }

    public void updateHologram() {
        spawned.forEach(this::updateHologram);
    }

    public void updateHologram(final Player player) {
        if (!isSpawned(player)) return;
        // todo: properly implement this
        despawn(player);
        spawn(player);
    }

    public void updateVisibility() {
        spawned.forEach(this::updateVisibility);
    }

    public void updateVisibility(final Player player) {
        if (canSee(player)) spawn(player);
        else despawn(player);
    }

    public void updateText() {
        lines.forEach(hologramLine -> {
            if (!(hologramLine instanceof final PaperTextHologramLine line)) return;
            line.getEntities().forEach(line::updateText);
        });
    }

    public void updateText(final Player player) {
        lines.forEach(hologramLine -> {
            if (!(hologramLine instanceof final PaperTextHologramLine line)) return;
            line.getEntity(player, TextDisplay.class).ifPresent(textDisplay -> line.updateText(player, textDisplay));
        });
    }

    public void invalidate(final Entity entity) {
        lines.forEach(line -> {
            if (line instanceof final PaperHologramLine<?> paperLine) paperLine.invalidate(entity);
            else if (line instanceof final PaperPagedHologramLine pagedLine) pagedLine.invalidate(entity);
        });
    }

    @Override
    public CompoundTag serialize() throws ParserException {
        final var nbt = plugin.serializer(getWorld());
        final var builder = CompoundTag.builder();

        builder.put("position", nbt.serialize(location));
        builder.put("rotation", CompoundTag.builder()
                .put("yaw", location.getYaw())
                .put("pitch", location.getPitch())
                .build());
        builder.put("visibleByDefault", visibleByDefault);
        getViewPermission().ifPresent(permission -> builder.put("viewPermission", permission));

        final var lines = this.lines.stream().map(nbt::serialize).toList();
        if (!lines.isEmpty()) builder.put("lines", ListTag.of(lines));

        return builder.build();
    }

    @Override
    public void deserialize(final CompoundTag tag) throws ParserException {
        final var nbt = plugin.deserializer(this);

        tag.optional("position").map(tag1 -> nbt.deserialize(tag1, Position.class)).ifPresent(position -> {
            this.location.setX(position.x());
            this.location.setY(position.y());
            this.location.setZ(position.z());
        });
        tag.optional("rotation").map(Tag::getAsCompound).ifPresent(rotation -> {
            rotation.optional("yaw").map(Tag::getAsFloat).ifPresent(location::setYaw);
            rotation.optional("pitch").map(Tag::getAsFloat).ifPresent(location::setPitch);
        });

        tag.optional("viewPermission").map(Tag::getAsString).ifPresent(this::setViewPermission);
        tag.optional("visibleByDefault").map(Tag::getAsBoolean).ifPresent(this::setVisibleByDefault);

        tag.optional("lines").map(Tag::<CompoundTag>getAsList).ifPresent(lines -> {
            lines.stream().map(line -> {
                final var type = nbt.deserialize(line.get("lineType"), LineType.class);
                return nbt.<HologramLine>deserialize(line, switch (type) {
                    case ENTITY -> EntityHologramLine.class;
                    case BLOCK -> BlockHologramLine.class;
                    case ITEM -> ItemHologramLine.class;
                    case TEXT -> TextHologramLine.class;
                    case PAGED -> PagedHologramLine.class;
                });
            }).forEach(this.lines::add);
        });
    }
}
