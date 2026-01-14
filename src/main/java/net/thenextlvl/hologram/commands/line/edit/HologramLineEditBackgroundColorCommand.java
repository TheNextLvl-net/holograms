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
    private HologramLineEditBackgroundColorCommand(HologramPlugin plugin) {
        super(plugin, "background-color", "holograms.command.line.edit.background-color");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditBackgroundColorCommand(plugin);
        var named = Commands.argument("color", ArgumentTypes.namedColor());
        var hex = Commands.argument("hex", new ColorArgumentType());
        return command.create()
                .then(named.executes(command))
                .then(hex.executes(command))
                .then(Commands.literal("reset").executes(command));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var line = context.getArgument("line", int.class);
        var color = tryGetArgument(context, "hex", Color.class)
                .or(() -> tryGetArgument(context, "color", NamedTextColor.class)
                        .map(TextColor::value).map(Color::fromRGB))
                .orElse(null);

        var message = hologram.getLine(line - 1, TextHologramLine.class).map(textLine -> {
            if (Objects.equals(textLine.getBackgroundColor().orElse(null), color)) return "nothing.changed";
            textLine.setBackgroundColor(color);
            return color != null ? "hologram.text.background-color" : "hologram.text.background-color.reset";
        }).orElse("hologram.type.text");

        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("hologram", hologram.getName()),
                Formatter.number("line", line));
        return SINGLE_SUCCESS;
    }
}
