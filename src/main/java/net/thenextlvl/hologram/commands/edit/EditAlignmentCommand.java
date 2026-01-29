package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.arguments.EnumArgumentType;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import org.bukkit.entity.TextDisplay;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditAlignmentCommand extends EditCommand {
    private EditAlignmentCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "alignment", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditAlignmentCommand(plugin, resolver);
        final var named = Commands.argument("alignment", new EnumArgumentType<>(TextDisplay.TextAlignment.class));
        return command.create().then(named.executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var alignment = context.getArgument("alignment", TextDisplay.TextAlignment.class);
            final var message = set(line.getAlignment(), alignment, line::setAlignment, "hologram.text-alignment");

            final var alignmentName = plugin.bundle().component(switch (alignment) {
                case LEFT -> "text-alignment.left";
                case CENTER -> "text-alignment.center";
                case RIGHT -> "text-alignment.right";
            }, context.getSource().getSender());

            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    concat(placeholders, Placeholder.component("alignment", alignmentName)));
            return SINGLE_SUCCESS;
        }, LineType.TEXT);
    }
}
