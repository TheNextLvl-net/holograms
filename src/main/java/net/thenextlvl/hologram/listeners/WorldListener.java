package net.thenextlvl.hologram.listeners;

import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.nbt.NBTInputStream;
import net.thenextlvl.nbt.serialization.ParserException;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.READ;
import static net.thenextlvl.hologram.HologramPlugin.ISSUES;

@NullMarked
public class WorldListener implements Listener {
    private final HologramPlugin plugin;

    public WorldListener(HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        var dataFolder = plugin.hologramController().getDataFolder(event.getWorld());
        if (!Files.isDirectory(dataFolder)) return;
        try (var files = Files.find(dataFolder, 1, (path, attributes) -> {
            return attributes.isRegularFile() && path.getFileName().toString().endsWith(".dat");
        })) {
            files.forEach(path -> loadSafe(path, event.getWorld()));
        } catch (IOException e) {
            plugin.getComponentLogger().error("Failed to load all holograms in world {}", event.getWorld().getName(), e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldSave(WorldSaveEvent event) {
        plugin.hologramController().getHolograms(event.getWorld()).forEach(Hologram::persist);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
        plugin.hologramController().holograms.removeIf(hologram -> {
            if (hologram.getWorld().equals(event.getWorld())) {
                hologram.persist();
                return true;
            } else return false;
        });
    }

    private @Nullable Hologram loadSafe(Path file, World world) {
        try {
            try (var inputStream = stream(file)) {
                return load(inputStream, world);
            } catch (Exception e) {
                var backup = file.resolveSibling(file.getFileName() + "_old");
                if (!Files.isRegularFile(backup)) throw e;
                plugin.getComponentLogger().warn("Failed to load hologram from {}", file, e);
                plugin.getComponentLogger().warn("Falling back to {}", backup);
                try (var inputStream = stream(backup)) {
                    return load(inputStream, world);
                }
            }
        } catch (ParserException e) {
            plugin.getComponentLogger().warn("Failed to load hologram from {}: {}", file, e.getMessage());
            return null;
        } catch (EOFException e) {
            plugin.getComponentLogger().error("The hologram file {} is irrecoverably broken", file);
            return null;
        } catch (Exception e) {
            plugin.getComponentLogger().error("Failed to load hologram from {}", file, e);
            plugin.getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            return null;
        }
    }

    private NBTInputStream stream(Path file) throws IOException {
        return new NBTInputStream(Files.newInputStream(file, READ), StandardCharsets.UTF_8);
    }

    private @Nullable Hologram load(NBTInputStream inputStream, World world) throws IOException {
        var hologram = plugin.nbt(world).deserialize(inputStream.readTag(), Hologram.class);
        if (plugin.hologramController().holograms.add(hologram)) return hologram;
        plugin.getComponentLogger().warn("A hologram with the name '{}' is already loaded", hologram.getName());
        return null;
    }
}
