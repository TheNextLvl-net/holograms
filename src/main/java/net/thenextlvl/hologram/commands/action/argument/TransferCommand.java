package net.thenextlvl.hologram.commands.action.argument;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ActionTypes;
import net.thenextlvl.hologram.commands.action.ActionTargetResolver;
import org.jspecify.annotations.NullMarked;

import java.net.InetSocketAddress;

@NullMarked
public final class TransferCommand extends HologramActionCommand<InetSocketAddress> {
    private TransferCommand(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        super(plugin, ActionTypes.types().transfer(), "transfer", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        final var command = new TransferCommand(plugin, resolver);
        final var hostname = Commands.argument("hostname", StringArgumentType.string()).executes(command);
        final var port = Commands.argument("port", IntegerArgumentType.integer(1, 65535)).executes(command);
        return command.create().then(hostname.then(port));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return resolverBuilder.build(context, plugin).resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var hostname = context.getArgument("hostname", String.class);
            final var port = tryGetArgument(context, "port", int.class).orElse(25565);
            return addAction(context, hologram, line, new InetSocketAddress(hostname, port));
        });
    }
}
