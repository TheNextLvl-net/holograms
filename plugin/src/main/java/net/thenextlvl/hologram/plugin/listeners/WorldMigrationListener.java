package net.thenextlvl.hologram.plugin.listeners;

import net.thenextlvl.hologram.plugin.HologramPlugin;
import net.thenextlvl.worlds.event.WorldFolderMigrateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.nio.file.Files;

import static net.thenextlvl.hologram.plugin.HologramPlugin.ISSUES;

@NullMarked
public final class WorldMigrationListener implements Listener {
    private final HologramPlugin plugin;

    public WorldMigrationListener(final HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldFolderMigrate(final WorldFolderMigrateEvent event) {
        final var oldPath = event.getOldFolder().resolve("holograms");
        if (!Files.isDirectory(oldPath)) return;

        final var dataFolder = event.getNewFolder().resolve("data");
        final var newPath = dataFolder.resolve("holograms");
        try {
            Files.createDirectories(dataFolder);
            Files.move(oldPath, newPath);
            plugin.getComponentLogger().info("Migrated holograms from {} to {}", oldPath, newPath);
        } catch (final IOException e) {
            plugin.getComponentLogger().error("Failed to migrate holograms from {} to {}", oldPath, newPath, e);
            plugin.getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            HologramPlugin.ERROR_TRACKER.trackError(e);
        }
    }
}
