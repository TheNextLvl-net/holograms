package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditGlowColorCommand extends EditCommand {
    private EditGlowColorCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "glow-color", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditGlowColorCommand(plugin, resolver);
        final var hex = Commands.argument("hex", ArgumentTypes.hexColor())
                .suggests((context, builder) -> builder.buildFuture());
        final var named = Commands.argument("color", ArgumentTypes.namedColor());
        return command.create()
                .then(hex.executes(command))
                .then(named.executes(command))
                .then(Commands.literal("reset").executes(command::reset))
                .executes(command);
    }

    private int reset(final CommandContext<CommandSourceStack> context) {
        final var resolver = this.resolver.build(context, this.plugin);
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var message = set(line.getGlowColor().orElse(null), null, line::setGlowColor, "hologram.glow-color");
            plugin.bundle().sendMessage(context.getSource().getSender(), message, TagResolver.resolver(placeholders),
                    Placeholder.unparsed("color", "none"));
            return SINGLE_SUCCESS;
        }, LineType.DISPLAY);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var color = tryGetArgument(context, "hex", TextColor.class)
                    .or(() -> tryGetArgument(context, "color", NamedTextColor.class));
            final var ored = color.or(line::getGlowColor).orElse(null);
            final var message = color.map(c -> {
                return set(line.getGlowColor().orElse(null), c, line::setGlowColor, "hologram.glow-color");
            }).orElse(ored != null ? "hologram.glow-color.query" : "hologram.glow-color.query.none");
            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    TagResolver.resolver(placeholders),
                    Placeholder.unparsed("color", ored != null ? ored.toString() : "none"));
            return SINGLE_SUCCESS;
        }, LineType.DISPLAY);
    }
}
