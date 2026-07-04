package net.thenextlvl.hologram.plugin.version;

import net.thenextlvl.hologram.plugin.HologramPlugin;
import net.thenextlvl.version.SemanticVersion;
import net.thenextlvl.version.modrinth.paper.PaperModrinthVersionChecker;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PluginVersionChecker extends PaperModrinthVersionChecker<SemanticVersion> {
    public PluginVersionChecker(final Plugin plugin) {
        super(plugin, "yWs5IQBz");
    }

    @Override
    public String getLoader() {
        return HologramPlugin.RUNNING_FOLIA ? "folia" : "paper";
    }

    @Override
    public SemanticVersion parseVersion(final String version) {
        return SemanticVersion.parse(version);
    }
}
