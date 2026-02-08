package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditOpacityCommand extends EditCommand {
    private EditOpacityCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "opacity", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditOpacityCommand(plugin, resolver);
        final var opacity = Commands.argument("opacity", IntegerArgumentType.integer(0, 100));
        return command.create().then(opacity.executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var opacity = context.getArgument("opacity", int.class);
            final var message = set(line.getTextOpacity(), opacity, line::setTextOpacity, "hologram.line.opacity");
            plugin.bundle().sendMessage(context.getSource().getSender(), message, placeholders);
            return SINGLE_SUCCESS;
        }, LineType.TEXT);
    }
}
