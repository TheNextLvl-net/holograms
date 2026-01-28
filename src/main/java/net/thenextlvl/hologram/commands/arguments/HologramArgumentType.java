package net.thenextlvl.hologram.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.line.LineType;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public final class HologramArgumentType implements CustomArgumentType.Converted<Hologram, String> {
    private final HologramPlugin plugin;
    private final boolean pagedOnly;

    public HologramArgumentType(final HologramPlugin plugin, final boolean pagedOnly) {
        this.plugin = plugin;
        this.pagedOnly = pagedOnly;
    }

    @Override
    public Hologram convert(final String nativeType) {
        return plugin.hologramProvider().getHologram(nativeType)
                .orElseThrow(() -> new NullPointerException("No hologram was found"));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        plugin.hologramProvider().getHolograms()
                .filter(name -> !pagedOnly || name.getLines().anyMatch(line -> line.getType() == LineType.PAGED))
                .map(Hologram::getName)
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
