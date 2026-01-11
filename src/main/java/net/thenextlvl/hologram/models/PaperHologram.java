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
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.hologram.models.line.PaperBlockHologramLine;
import net.thenextlvl.hologram.models.line.PaperEntityHologramLine;
import net.thenextlvl.hologram.models.line.PaperHologramLine;
import net.thenextlvl.hologram.models.line.PaperItemHologramLine;
import net.thenextlvl.hologram.models.line.PaperTextHologramLine;
import net.thenextlvl.nbt.NBTOutputStream;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagSerializable;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.ListTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@NullMarked
public class PaperHologram implements Hologram, TagSerializable<CompoundTag> {
    private final List<HologramLine<?>> lines = new LinkedList<>();
    private final Set<UUID> viewers = new HashSet<>();

    private final HologramPlugin plugin;

    private String name;

    private Path dataFile;
    private Path backupFile;

    private Location location;
    private @Nullable String viewPermission;

    private boolean persistent = true;
    private boolean visibleByDefault = true;
    private boolean spawned = false;

    public PaperHologram(HologramPlugin plugin, String name, Location location) {
        Preconditions.checkArgument(location.getWorld() != null, "World cannot be null");

        this.plugin = plugin;
        this.location = location;
        this.name = name;

        var dataFolder = plugin.hologramProvider().getDataFolder(location.getWorld());
        this.dataFile = dataFolder.resolve(name + ".dat");
        this.backupFile = dataFolder.resolve(name + ".dat_old");
    }

    public PaperHologram(HologramPlugin plugin, String name, World world) {
        this(plugin, name, new Location(world, 0, 0, 0));
    }

    public HologramPlugin getPlugin() {
        return plugin;
    }

    public Stream<? extends Entity> getEntities() {
        return lines.stream()
                .map(hologramLine -> hologramLine.getEntity().orElse(null))
                .filter(Objects::nonNull);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean setName(String name) {
        if (Objects.equals(this.name, name)) return false;
        if (plugin.hologramProvider().hasHologram(name)) return false;

        if (!updatePaths(getWorld(), name)) return false;

        this.name = name;
        return true;
    }

    private boolean updatePaths(World world, String name) {
        var dataFolder = plugin.hologramProvider().getDataFolder(world);
        var dataFile = dataFolder.resolve(name + ".dat");
        var backupFile = dataFolder.resolve(name + ".dat_old");

        try {
            Files.createDirectories(dataFolder);
            if (Files.isRegularFile(getDataFile())) Files.move(getDataFile(), dataFile, REPLACE_EXISTING);
            if (Files.isRegularFile(getBackupFile())) Files.move(getBackupFile(), backupFile, REPLACE_EXISTING);
        } catch (IOException e) {
            plugin.getComponentLogger().warn("Failed to move hologram data files for: {}", getName(), e);
            return false;
        }

        this.dataFile = dataFile;
        this.backupFile = backupFile;
        return true;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public World getWorld() {
        return location.getWorld();
    }

    @Override
    public CompletableFuture<Boolean> teleportAsync(Location location) {
        Preconditions.checkArgument(location.getWorld() != null, "World cannot be null");
        var previous = this.location;
        var success = setLocation(location);
        if (!success) return CompletableFuture.completedFuture(false);
        return CompletableFuture.allOf(lines.stream()
                .map(line -> (PaperHologramLine<?>) line)
                .map(line -> line.teleportRelative(previous, location))
                .toArray(CompletableFuture[]::new)
        ).thenApply(v -> true);
    }

    private boolean setLocation(Location location) {
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
    public @Unmodifiable List<HologramLine<?>> getLines() {
        return List.copyOf(lines);
    }

    @Override
    public int getLineCount() {
        return lines.size();
    }

    @Override
    public @Nullable HologramLine<?> getLine(int index) throws IndexOutOfBoundsException {
        return lines.get(index);
    }

    @Override
    public int getLineIndex(HologramLine<?> line) {
        return lines.indexOf(line);
    }

    @Override
    public boolean removeLine(HologramLine<?> line) {
        var removed = lines.remove(line);
        if (removed) line.getEntity().ifPresent(Entity::remove);
        updateHologram();
        return removed;
    }

    @Override
    public boolean removeLine(int index) {
        if (index < 0 || index >= lines.size()) return false;
        var removed = lines.remove(index);
        removed.getEntity().ifPresent(Entity::remove);
        updateHologram();
        return true;
    }

    @Override
    public boolean removeLines(Collection<HologramLine<?>> lines) {
        var removed = lines.stream().map(this::removeLine).reduce(false, Boolean::logicalOr);
        if (removed) updateHologram();
        return removed;
    }

    @Override
    public void clearLines() {
        if (lines.isEmpty()) return;
        getEntities().forEach(Entity::remove);
        lines.clear();
        updateHologram();
    }

    @Override
    public boolean hasLine(HologramLine<?> line) {
        return lines.contains(line);
    }

    @Override
    public boolean moveLine(int line, int index) {
        if (line == index) return false;
        if (line < 0 || line >= lines.size()) return false;
        if (index < 0 || index > lines.size()) return false;
        lines.add(index, lines.remove(line));
        updateHologram();
        return true;
    }

    @Override
    public boolean swapLines(int line1, int line2) {
        if (line1 == line2) return false;
        if (line1 < 0 || line1 >= lines.size()) return false;
        if (line2 < 0 || line2 >= lines.size()) return false;
        var hologramLine1 = lines.get(line1);
        var hologramLine2 = lines.get(line2);
        lines.set(line1, hologramLine2);
        lines.set(line2, hologramLine1);
        updateHologram();
        return true;
    }

    @Override
    public EntityHologramLine<?> addEntityLine(EntityType entityType) throws IllegalArgumentException {
        return addEntityLine(entityType, lines.size());
    }

    @Override
    public <T extends Entity> EntityHologramLine<T> addEntityLine(Class<T> entityType) throws IllegalArgumentException {
        return addEntityLine(entityType, lines.size());
    }

    @Override
    public EntityHologramLine<?> addEntityLine(EntityType entityType, int index) throws IllegalArgumentException {
        Preconditions.checkArgument(entityType.getEntityClass() != null, "Cannot spawn entity of type %s", entityType);
        return addEntityLine(entityType.getEntityClass(), index);
    }

    @Override
    public <T extends Entity> EntityHologramLine<T> addEntityLine(Class<T> entityType, int index) throws IllegalArgumentException {
        var hologramLine = new PaperEntityHologramLine<>(this, entityType);
        lines.add(index, hologramLine);
        updateHologram();
        return hologramLine;
    }

    @Override
    public BlockHologramLine addBlockLine() {
        return addBlockLine(lines.size());
    }

    @Override
    public BlockHologramLine addBlockLine(int index) {
        var hologramLine = new PaperBlockHologramLine(this);
        lines.add(index, hologramLine);
        updateHologram();
        return hologramLine;
    }

    @Override
    public ItemHologramLine addItemLine() {
        return addItemLine(lines.size());
    }

    @Override
    public ItemHologramLine addItemLine(int index) {
        var hologramLine = new PaperItemHologramLine(this);
        lines.add(index, hologramLine);
        updateHologram();
        return hologramLine;
    }

    @Override
    public TextHologramLine addTextLine() {
        return addTextLine(lines.size());
    }

    @Override
    public TextHologramLine addTextLine(int index) {
        var hologramLine = new PaperTextHologramLine(this);
        lines.add(index, hologramLine);
        updateHologram();
        return hologramLine;
    }

    @Override
    public EntityHologramLine<?> setEntityLine(EntityType entityType, int index) throws IllegalArgumentException {
        Preconditions.checkArgument(entityType.getEntityClass() != null, "Cannot spawn entity of type %s", entityType);
        return setEntityLine(entityType.getEntityClass(), index);
    }

    @Override
    public <T extends Entity> EntityHologramLine<T> setEntityLine(Class<T> entityType, int index) throws IllegalArgumentException {
        var hologramLine = new PaperEntityHologramLine<>(this, entityType);
        var previous = (PaperHologramLine<?>) lines.set(index, hologramLine);
        previous.despawn();
        updateHologram();
        return hologramLine;
    }

    @Override
    public BlockHologramLine setBlockLine(int index) {
        var hologramLine = new PaperBlockHologramLine(this);
        var previous = (PaperHologramLine<?>) lines.set(index, hologramLine);
        previous.despawn();
        updateHologram();
        return hologramLine;
    }

    @Override
    public ItemHologramLine setItemLine(int index) {
        var hologramLine = new PaperItemHologramLine(this);
        var previous = (PaperHologramLine<?>) lines.set(index, hologramLine);
        previous.despawn();
        updateHologram();
        return hologramLine;
    }

    @Override
    public TextHologramLine setTextLine(int index) {
        var hologramLine = new PaperTextHologramLine(this);
        var previous = (PaperHologramLine<?>) lines.set(index, hologramLine);
        previous.despawn();
        updateHologram();
        return hologramLine;
    }

    @Override
    public @Nullable String getViewPermission() {
        return viewPermission;
    }

    @Override
    public boolean setViewPermission(@Nullable String permission) {
        if (Objects.equals(this.viewPermission, permission)) return false;
        this.viewPermission = permission;
        lines.forEach(hologramLine -> plugin.getServer().getOnlinePlayers()
                .forEach(player -> ((PaperHologramLine<?>) hologramLine).updateVisibility(player)));
        return true;
    }

    @Override
    public @Unmodifiable Set<UUID> getViewers() {
        return Set.copyOf(viewers);
    }

    @Override
    public boolean addViewer(UUID player) {
        if (!viewers.add(player)) return false;
        if (lines.isEmpty() || isVisibleByDefault()) return true;
        var online = plugin.getServer().getPlayer(player);
        if (online != null) getEntities().forEach(entity -> online.showEntity(plugin, entity));
        return true;
    }

    @Override
    public boolean addViewers(Collection<UUID> players) {
        return players.stream().map(this::addViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean removeViewer(UUID player) {
        if (!viewers.remove(player)) return false;
        if (lines.isEmpty() || isVisibleByDefault()) return true;
        var online = plugin.getServer().getPlayer(player);
        if (online != null) getEntities().forEach(entity -> online.hideEntity(plugin, entity));
        return true;
    }

    @Override
    public boolean removeViewers(Collection<UUID> players) {
        return players.stream().map(this::removeViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean isViewer(UUID player) {
        return viewers.contains(player);
    }

    @Override
    public boolean canSee(Player player) {
        if (lines.isEmpty() || !isSpawned()) return false;
        if (!player.getWorld().equals(location.getWorld())) return false;
        if (viewPermission != null && !player.hasPermission(viewPermission)) return false;
        return isVisibleByDefault() || isViewer(player.getUniqueId());
    }

    @Override
    public boolean isTrackedBy(Player player) {
        return getEntities().anyMatch(entity -> entity.getTrackedBy().contains(player));
    }

    @Override
    public boolean isVisibleByDefault() {
        return visibleByDefault;
    }

    @Override
    public boolean setVisibleByDefault(boolean visible) {
        if (this.visibleByDefault == visible) return false;
        this.visibleByDefault = visible;
        return true;
    }

    @Override
    public boolean setPersistent(boolean persistent) {
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
        var file = getDataFile();
        var backup = getBackupFile();
        try {
            if (Files.isRegularFile(file)) Files.move(file, backup, REPLACE_EXISTING);
            else Files.createDirectories(file.getParent());
            try (var outputStream = NBTOutputStream.create(file)) {
                outputStream.writeTag(getName(), serialize());
                return true;
            }
        } catch (Throwable t) {
            if (Files.isRegularFile(backup)) try {
                Files.copy(backup, file, REPLACE_EXISTING);
                plugin.getComponentLogger().warn("Recovered hologram {} from potential data loss", getName());
            } catch (IOException e) {
                plugin.getComponentLogger().error("Failed to restore hologram {}", getName(), e);
            }
            plugin.getComponentLogger().error("Failed to save hologram {}", getName(), t);
            plugin.getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", HologramPlugin.ISSUES);
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
    public boolean spawn() {
        if (isSpawned() || !getLocation().isChunkLoaded()) return false;
        var offset = 0d;
        for (var index = lines.size() - 1; index >= 0; index--) {
            var line = lines.get(index);
            var hologramLine = (PaperHologramLine<?>) line;
            var spawn = hologramLine.spawn(offset + hologramLine.getOffsetBefore());
            offset += 0.05 + hologramLine.getHeight() + hologramLine.getOffsetAfter();
            // fixme: last line height for multiline texts is not counted correctly
        }
        this.spawned = true;
        return true;
    }

    @Override
    public Iterator<HologramLine<?>> iterator() {
        return getLines().iterator();
    }

    @Override
    public void despawn() {
        lines.forEach(hologramLine -> ((PaperHologramLine<?>) hologramLine).despawn());
        this.spawned = false;
    }

    public void updateHologram() {
        if (!isSpawned()) return;
        // todo: properly implement this
        despawn();
        spawn();
    }

    @Override
    public boolean isSpawned() {
        return spawned;
    }

    public void invalidate(Entity entity) {
        lines.forEach(line -> ((PaperHologramLine<?>) line).invalidate(entity));
    }

    @Override
    public CompoundTag serialize() throws ParserException {
        var nbt = plugin.serializer(getWorld());
        var builder = CompoundTag.builder();

        builder.put("position", nbt.serialize(location));
        builder.put("visibleByDefault", visibleByDefault);
        if (viewPermission != null) builder.put("viewPermission", viewPermission);

        var lines = this.lines.stream().map(nbt::serialize).toList();
        if (!lines.isEmpty()) builder.put("lines", ListTag.of(lines));

        return builder.build();
    }

    @Override
    public void deserialize(CompoundTag tag) throws ParserException {
        var nbt = plugin.deserializer(this);

        tag.optional("position").map(tag1 -> nbt.deserialize(tag1, Position.class))
                .map(position -> position.toLocation(getWorld()))
                .ifPresent(this::setLocation);
        tag.optional("viewPermission").map(Tag::getAsString).ifPresent(this::setViewPermission);
        tag.optional("visibleByDefault").map(Tag::getAsBoolean).ifPresent(this::setVisibleByDefault);

        tag.optional("lines").map(Tag::<CompoundTag>getAsList).ifPresent(lines -> {
            lines.stream().map(line -> {
                var type = nbt.deserialize(line.get("lineType"), LineType.class);
                return nbt.<HologramLine<?>>deserialize(line, switch (type) {
                    case ENTITY -> EntityHologramLine.class;
                    case BLOCK -> BlockHologramLine.class;
                    case ITEM -> ItemHologramLine.class;
                    case TEXT -> TextHologramLine.class;
                });
            }).forEach(this.lines::add);
        });
    }
}
