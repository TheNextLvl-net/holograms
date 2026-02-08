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
public final class CyclePageCommand extends HologramActionCommand<Integer> {
    private CyclePageCommand(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        super(plugin, ActionTypes.types().cyclePage(), "cycle-page", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        final var command = new CyclePageCommand(plugin, resolver);
        final var amount = Commands.argument("amount", IntegerArgumentType.integer());
        return command.create()
                .then(amount.executes(command))
                .executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return resolverBuilder.build(context, plugin).resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var amount = tryGetArgument(context, "amount", int.class).orElse(1);
            return addAction(context, hologram, line, amount);
        });
    }
}
