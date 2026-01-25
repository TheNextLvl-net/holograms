package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.arguments.StringArgumentType;
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
final class HologramLineEditAppendCommand extends SimpleCommand {
    private HologramLineEditAppendCommand(final HologramPlugin plugin) {
        super(plugin, "append", "holograms.command.line.edit.append");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineEditAppendCommand(plugin);
        final var text = Commands.argument("text", StringArgumentType.greedyString());
        return command.create().then(text.executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var text = context.getArgument("text", String.class);
        final var line = context.getArgument("line", int.class);

        final var message = hologram.getLine(line - 1, TextHologramLine.class).map(textLine -> {
            if (text.isEmpty()) return "nothing.changed";
            textLine.getUnparsedText().map(string -> string.concat(text))
                    .ifPresent(textLine::setUnparsedText);
            return "hologram.text.set";
        }).orElse("hologram.type.text");

        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", line));
        return SINGLE_SUCCESS;
    }
}
