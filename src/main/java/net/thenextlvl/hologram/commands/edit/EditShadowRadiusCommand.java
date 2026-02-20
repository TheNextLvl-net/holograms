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
final class EditShadowRadiusCommand extends EditCommand {
    private EditShadowRadiusCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "shadow-radius", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditShadowRadiusCommand(plugin, resolver);
        final var radius = Commands.argument("radius", FloatArgumentType.floatArg(0));
        return command.create().then(radius.executes(command)).executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var radius = tryGetArgument(context, "radius", float.class);

            final var message = radius.map(value -> {
                return set(value, line::setShadowRadius, "hologram.shadow-radius");
            }).orElse("hologram.shadow-radius.query");

            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    TagResolver.resolver(placeholders),
                    Formatter.number("radius", radius.orElseGet(line::getShadowRadius)));
            return SINGLE_SUCCESS;
        }, LineType.DISPLAY);
    }
}
