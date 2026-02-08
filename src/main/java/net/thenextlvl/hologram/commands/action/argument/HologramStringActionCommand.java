package net.thenextlvl.hologram.commands.action.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ActionType;
import net.thenextlvl.hologram.commands.action.ActionTargetResolver;
import org.jspecify.annotations.NullMarked;

@NullMarked
abstract class HologramStringActionCommand extends HologramActionCommand<String> {
    private final String argument;

    protected HologramStringActionCommand(final HologramPlugin plugin, final ActionType<String> actionType, final String name, final String argument, final ActionTargetResolver.Builder resolver) {
        super(plugin, actionType, name, resolver);
        this.argument = argument;
    }

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> create() {
        final var argument = Commands.argument(this.argument, StringArgumentType.greedyString());
        return super.create().then(argument.executes(this));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return resolverBuilder.build(context, plugin).resolve((hologram, line, lineIndex, pageIndex, placeholders) ->
                addAction(context, hologram, line, context.getArgument(argument, String.class)));
    }
}
