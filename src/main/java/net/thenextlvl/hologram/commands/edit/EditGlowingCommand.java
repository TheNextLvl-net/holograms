package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditGlowingCommand extends EditCommand {
    private EditGlowingCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "glowing", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditGlowingCommand(plugin, resolver);
        final var named = Commands.argument("glowing", BoolArgumentType.bool());
        return command.create().then(named.executes(command)).executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var glowing = tryGetArgument(context, "glowing", boolean.class);

            final var message = glowing.map(value -> {
                final var successKey = value ? "hologram.line.glowing.enabled" : "hologram.line.glowing.disabled";
                return set(value, line::setGlowing, successKey);
            }).orElse("hologram.glowing.query");

            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    TagResolver.resolver(placeholders),
                    Placeholder.unparsed("state", String.valueOf(glowing.orElseGet(line::isGlowing))));
            return SINGLE_SUCCESS;
        }, LineType.DISPLAY);
    }
}
