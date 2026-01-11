package net.thenextlvl.hologram.version;

import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.version.SemanticVersion;
import net.thenextlvl.version.modrinth.paper.PaperModrinthVersionChecker;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PluginVersionChecker extends PaperModrinthVersionChecker<SemanticVersion> {
    public PluginVersionChecker(Plugin plugin) {
        super(plugin, "yWs5IQBz");
    }

    @Override
    public String getLoader() {
        return HologramPlugin.RUNNING_FOLIA ? "folia" : "paper";
    }

    @Override
    public SemanticVersion parseVersion(String version) {
        return SemanticVersion.parse(version);
    }
}
