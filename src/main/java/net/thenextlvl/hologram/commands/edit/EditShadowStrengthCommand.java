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
final class EditShadowStrengthCommand extends EditCommand {
    private EditShadowStrengthCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "shadow-strength", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditShadowStrengthCommand(plugin, resolver);
        final var strength = Commands.argument("strength", FloatArgumentType.floatArg(0));
        return command.create().then(strength.executes(command)).executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var strength = tryGetArgument(context, "strength", float.class);

            final var message = strength.map(value -> {
                return set(line.getShadowStrength(), value, line::setShadowStrength, "hologram.shadow-strength");
            }).orElse("hologram.shadow-strength.query");

            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    TagResolver.resolver(placeholders),
                    Formatter.number("strength", strength.orElseGet(line::getShadowStrength)));
            return SINGLE_SUCCESS;
        }, LineType.DISPLAY);
    }
}
