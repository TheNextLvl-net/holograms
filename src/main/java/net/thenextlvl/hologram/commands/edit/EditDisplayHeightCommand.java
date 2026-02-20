package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditDisplayHeightCommand extends EditCommand {
    private EditDisplayHeightCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "display-height", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditDisplayHeightCommand(plugin, resolver);
        final var height = Commands.argument("height", FloatArgumentType.floatArg(0));
        return command.create().then(height.executes(command)).executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var height = tryGetArgument(context, "height", float.class);

            final var message = height.map(value -> {
                return set(value, line::setDisplayHeight, "hologram.display-height");
            }).orElse("hologram.display-height.query");

            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    TagResolver.resolver(placeholders),
                    Formatter.number("height", height.orElseGet(line::getDisplayHeight)));
            return SINGLE_SUCCESS;
        }, LineType.DISPLAY);
    }
}
