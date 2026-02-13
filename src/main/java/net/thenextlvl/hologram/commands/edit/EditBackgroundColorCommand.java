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
import net.thenextlvl.hologram.commands.arguments.ColorArgumentType;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import org.bukkit.Color;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditBackgroundColorCommand extends EditCommand {
    private EditBackgroundColorCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "background-color", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditBackgroundColorCommand(plugin, resolver);
        final var named = Commands.argument("color", ArgumentTypes.namedColor());
        final var hex = Commands.argument("hex", new ColorArgumentType());
        return command.create()
                .then(named.executes(command))
                .then(hex.executes(command))
                .then(Commands.literal("reset").executes(command::reset))
                .executes(command);
    }

    private int reset(final CommandContext<CommandSourceStack> context) {
        final var resolver = this.resolver.build(context, this.plugin);
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var message = set(line.getBackgroundColor().orElse(null), null, line::setBackgroundColor, "hologram.text.background-color.reset");
            plugin.bundle().sendMessage(context.getSource().getSender(), message, placeholders);
            return SINGLE_SUCCESS;
        }, LineType.TEXT);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var color = tryGetArgument(context, "hex", Color.class)
                    .or(() -> tryGetArgument(context, "color", NamedTextColor.class)
                            .map(TextColor::value).map(Color::fromRGB));
            final var message = color.map(c -> {
                return set(line.getBackgroundColor().orElse(null), c, line::setBackgroundColor, "hologram.text.background-color");
            }).orElseGet(() -> {
                return line.getBackgroundColor().isPresent() ? "hologram.background-color.query" : "hologram.background-color.query.none";
            });
            final var currentColor = color.or(line::getBackgroundColor)
                    .map(Color::asARGB)
                    .map(Integer::toHexString)
                    .map("#"::concat)
                    .orElse("none");
            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    TagResolver.resolver(placeholders),
                    Placeholder.unparsed("color", currentColor));
            return SINGLE_SUCCESS;
        }, LineType.TEXT);
    }
}
