package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditDefaultBackgroundCommand extends SimpleCommand {
    private HologramLineEditDefaultBackgroundCommand(final HologramPlugin plugin) {
        super(plugin, "default-background", "holograms.command.line.edit.default-background");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineEditDefaultBackgroundCommand(plugin);
        final var defaultBackground = Commands.argument("default-background", BoolArgumentType.bool());
        return command.create().then(defaultBackground.executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();
        final var defaultBackground = context.getArgument("default-background", boolean.class);
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var line = context.getArgument("line", int.class);

        final var message = hologram.getLine(line - 1).map(hologramLine -> {
            if (!(hologramLine instanceof final TextHologramLine textLine))
                return "hologram.type.text";
            if (textLine.isDefaultBackground() == defaultBackground) return "nothing.changed";
            textLine.setDefaultBackground(defaultBackground);
            return defaultBackground ? "hologram.line.default-background.enabled" : "hologram.line.default-background.disabled";
        }).orElse("hologram.line.invalid");

        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", line));
        return SINGLE_SUCCESS;
    }
}
