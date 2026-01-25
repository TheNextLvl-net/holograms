package net.thenextlvl.hologram.commands.suggestions;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.thenextlvl.hologram.HologramPlugin;
import org.bukkit.permissions.Permission;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public final class PermissionSuggestionProvider<T> implements SuggestionProvider<T> {
    private final HologramPlugin plugin;

    public PermissionSuggestionProvider(final HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(final CommandContext<T> context, final SuggestionsBuilder builder) {
        plugin.getServer().getPluginManager().getPermissions().stream()
                .map(Permission::getName)
                .map(StringArgumentType::escapeIfRequired)
                .filter(string -> string.toLowerCase().contains(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
