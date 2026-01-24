package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.arguments.ColorArgumentType;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.bukkit.Color;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
final class HologramLineEditBackgroundColorCommand extends SimpleCommand {
    private HologramLineEditBackgroundColorCommand(final HologramPlugin plugin) {
        super(plugin, "background-color", "holograms.command.line.edit.background-color");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineEditBackgroundColorCommand(plugin);
        final var named = Commands.argument("color", ArgumentTypes.namedColor());
        final var hex = Commands.argument("hex", new ColorArgumentType());
        return command.create()
                .then(named.executes(command))
                .then(hex.executes(command))
                .then(Commands.literal("reset").executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var line = context.getArgument("line", int.class);
        final var color = tryGetArgument(context, "hex", Color.class)
                .or(() -> tryGetArgument(context, "color", NamedTextColor.class)
                        .map(TextColor::value).map(Color::fromRGB))
                .orElse(null);

        final var message = hologram.getLine(line - 1, TextHologramLine.class).map(textLine -> {
            if (Objects.equals(textLine.getBackgroundColor().orElse(null), color)) return "nothing.changed";
            textLine.setBackgroundColor(color);
            return color != null ? "hologram.text.background-color" : "hologram.text.background-color.reset";
        }).orElse("hologram.type.text");

        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", line));
        return SINGLE_SUCCESS;
    }
}
