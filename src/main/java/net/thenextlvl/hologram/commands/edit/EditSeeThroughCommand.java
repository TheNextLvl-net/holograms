package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditSeeThroughCommand extends EditCommand {
    private EditSeeThroughCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "see-through", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditSeeThroughCommand(plugin, resolver);
        final var named = Commands.argument("see-through", BoolArgumentType.bool());
        return command.create().then(named.executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var seeThrough = context.getArgument("see-through", boolean.class);
            final var successKey = seeThrough ? "hologram.line.see-through.enabled" : "hologram.line.see-through.disabled";
            final var message = set(line.isSeeThrough(), seeThrough, line::setSeeThrough, successKey);
            plugin.bundle().sendMessage(context.getSource().getSender(), message, placeholders);
            return SINGLE_SUCCESS;
        }, LineType.TEXT);
    }
}
