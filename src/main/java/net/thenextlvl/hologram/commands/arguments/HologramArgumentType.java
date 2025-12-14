package net.thenextlvl.hologram.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public final class HologramArgumentType implements CustomArgumentType.Converted<Hologram, String> {
    private final HologramPlugin plugin;

    public HologramArgumentType(HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Hologram convert(String nativeType) {
        return plugin.hologramController().getHologram(nativeType)
                .orElseThrow(() -> new NullPointerException("No hologram was found"));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        plugin.hologramController().getHologramNames()
                .map(StringArgumentType::escapeIfRequired)
                .filter(name -> name.toLowerCase().contains(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }
}
