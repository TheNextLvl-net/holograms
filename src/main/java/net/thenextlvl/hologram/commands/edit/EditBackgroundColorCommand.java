package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
                .then(Commands.literal("reset").executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var color = tryGetArgument(context, "hex", Color.class)
                    .or(() -> tryGetArgument(context, "color", NamedTextColor.class)
                            .map(TextColor::value).map(Color::fromRGB)).orElse(null);
            final var successKey = color != null ? "hologram.text.background-color" : "hologram.text.background-color.reset";
            final var message = set(line.getBackgroundColor().orElse(null), color, line::setBackgroundColor, successKey);
            plugin.bundle().sendMessage(context.getSource().getSender(), message, placeholders);
            return SINGLE_SUCCESS;
        }, LineType.TEXT);
    }
}
