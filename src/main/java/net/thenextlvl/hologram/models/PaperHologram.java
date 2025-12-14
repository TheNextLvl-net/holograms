package net.thenextlvl.hologram.models;

import com.google.common.base.Preconditions;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.hologram.models.line.PaperBlockHologramLine;
import net.thenextlvl.hologram.models.line.PaperEntityHologramLine;
import net.thenextlvl.hologram.models.line.PaperHologramLine;
import net.thenextlvl.hologram.models.line.PaperItemHologramLine;
import net.thenextlvl.hologram.models.line.PaperTextHologramLine;
import net.thenextlvl.nbt.NBTOutputStream;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

@NullMarked
public class PaperHologram implements Hologram {
    private final List<HologramLine<?>> lines = new LinkedList<>();
    private final Set<UUID> viewers = new HashSet<>();

    private final HologramPlugin plugin;
    private final String name;

    private @Nullable String viewPermission;
    private Location location;
    private boolean persistent;
    private boolean visibleByDefault = true;

    private boolean spawned = false;

    private Path dataFile;
    private Path backupFile;

    public PaperHologram(HologramPlugin plugin, String name, Location location) {
        Preconditions.checkArgument(location.getWorld() != null, "World cannot be null");

        this.plugin = plugin;
        this.location = location;
        this.name = name;

        var dataFolder = plugin.hologramController().getDataFolder(location.getWorld());
        this.dataFile = dataFolder.resolve(name + ".dat");
        this.backupFile = dataFolder.resolve(name + ".dat_old");
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
        var success = setLocation(location);
        return CompletableFuture.completedFuture(success); // todo: teleport all lines
    }

    private boolean setLocation(Location location) {
        if (this.location.equals(location)) return false;

        if (this.location.getWorld().equals(location.getWorld())) {
            this.location = location;
            return true;
        }

        var target = plugin.hologramController().getDataFolder(location.getWorld());
        var dataFile = target.resolve(getDataFile().getFileName());
        var backupFile = target.resolve(getBackupFile().getFileName());

        try {
            Files.createDirectories(target);
            if (Files.isRegularFile(getDataFile())) Files.move(getDataFile(), dataFile, REPLACE_EXISTING);
            if (Files.isRegularFile(getBackupFile())) Files.move(getBackupFile(), backupFile, REPLACE_EXISTING);
        } catch (IOException e) {
            plugin.getComponentLogger().warn("Failed to move hologram data files for: {}", getName(), e);
            return false;
        }

        this.dataFile = dataFile;
        this.backupFile = backupFile;
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
        return removed;
    }

    @Override
    public boolean removeLine(int index) throws IndexOutOfBoundsException {
        var removed = lines.remove(index);
        removed.getEntity().ifPresent(Entity::remove);
        return true;
    }

    @Override
    public boolean removeLines(Collection<HologramLine<?>> lines) {
        return lines.stream().map(this::removeLine).reduce(false, Boolean::logicalOr);
    }

    @Override
    public void clearLines() {
        getEntities().forEach(Entity::remove);
        lines.clear();
    }

    @Override
    public boolean hasLine(HologramLine<?> line) {
        return lines.contains(line);
    }

    @Override
    public EntityHologramLine<?> addEntityLine(EntityType entityType) throws IllegalArgumentException {
        Preconditions.checkArgument(entityType.getEntityClass() != null, "Cannot spawn entity of type %s", entityType);
        return addEntityLine(entityType.getEntityClass(), lines.size());
    }

    @Override
    public <T extends Entity> EntityHologramLine<T> addEntityLine(Class<T> entityType) throws IllegalArgumentException {
        return addEntityLine(entityType, lines.size());
    }

    @Override
    public <T extends Entity> EntityHologramLine<T> addEntityLine(Class<T> entityType, int index) throws IllegalArgumentException {
        var hologramLine = new PaperEntityHologramLine<>(this, entityType);
        lines.add(index, hologramLine);
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
            try (var outputStream = new NBTOutputStream(
                    Files.newOutputStream(file, WRITE, CREATE, TRUNCATE_EXISTING),
                    StandardCharsets.UTF_8
            )) {
                outputStream.writeTag(getName(), plugin.nbt(getWorld()).serialize(this));
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
        if (isSpawned()) return false;
        lines.forEach(HologramLine::spawn);
        this.spawned = true;
        return true;
    }

    @Override
    public Iterator<HologramLine<?>> iterator() {
        return getLines().iterator();
    }

    @Override
    public void despawn() {
        lines.forEach(HologramLine::despawn);
        this.spawned = false;
    }

    @Override
    public boolean isSpawned() {
        return spawned;
    }

    public void invalidate() {
        lines.forEach(line -> ((PaperHologramLine<?>) line).invalidate());
    }
}
