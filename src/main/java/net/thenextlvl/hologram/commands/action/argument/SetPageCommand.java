package net.thenextlvl.hologram.commands.action.argument;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ActionTypes;
import net.thenextlvl.hologram.action.PageChange;
import net.thenextlvl.hologram.commands.action.ActionTargetResolver;
import net.thenextlvl.hologram.commands.arguments.HologramArgumentType;
import net.thenextlvl.hologram.commands.suggestions.LineSuggestionProvider;
import net.thenextlvl.hologram.commands.suggestions.PageSuggestionProvider;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class SetPageCommand extends HologramActionCommand<PageChange> {
    private SetPageCommand(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        super(plugin, ActionTypes.types().setPage(), "set-page", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        final var command = new SetPageCommand(plugin, resolver);
        final var page = Commands.argument("target-page", IntegerArgumentType.integer(1))
                .suggests(new PageSuggestionProvider());
        final var line = Commands.argument("target-line", IntegerArgumentType.integer())
                .suggests(new LineSuggestionProvider(true, "target"));
        final var hologram = Commands.argument("target", new HologramArgumentType(plugin, true));
        return command.create()
                .then(page.executes(command))
                .then(hologram.then(line
                        .then(page.executes(command))
                        .executes(command)))
                .executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return resolverBuilder.build(context, plugin).resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final int targetPage = tryGetArgument(context, "target-page", int.class).map(integer -> integer - 1).orElse(1);
            final var targetHologram = tryGetArgument(context, "target", Hologram.class).orElse(null);
            final var targetLine = tryGetArgument(context, "target-line", int.class).map(i -> i - 1).orElse(null);
            return addAction(context, hologram, line, new PageChange(targetHologram, targetLine, targetPage));
        });
    }
}
