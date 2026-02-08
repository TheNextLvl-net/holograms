package net.thenextlvl.hologram.commands.action.argument;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ActionTypes;
import net.thenextlvl.hologram.commands.action.ActionTargetResolver;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class SetPageCommand extends HologramActionCommand<Integer> {
    private SetPageCommand(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        super(plugin, ActionTypes.types().setPage(), "set-page", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        final var command = new SetPageCommand(plugin, resolver);
        final var page = Commands.argument("target-page", IntegerArgumentType.integer(1));
        return command.create().then(page.executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return resolverBuilder.build(context, plugin).resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var targetPage = context.getArgument("target-page", int.class);
            return addAction(context, hologram, line, targetPage - 1);
        });
    }
}
