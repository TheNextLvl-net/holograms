package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditTeleportDurationCommand extends EditCommand {
    private EditTeleportDurationCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "teleport-duration", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditTeleportDurationCommand(plugin, resolver);
        final var named = Commands.argument("duration", ArgumentTypes.time());
        return command.create().then(named.executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var duration = context.getArgument("duration", int.class);
            final var message = set(line.getTeleportDuration(), duration, line::setTeleportDuration, "hologram.teleport-duration");
            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    TagResolver.resolver(placeholders),
                    Formatter.number("duration", duration));
            return SINGLE_SUCCESS;
        }, LineType.DISPLAY);
    }
}
