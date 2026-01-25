package net.thenextlvl.hologram.commands.line;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.commands.suggestions.LineSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
final class HologramLineMoveCommand extends SimpleCommand {
    private HologramLineMoveCommand(final HologramPlugin plugin) {
        super(plugin, "move", "holograms.command.line.move");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineMoveCommand(plugin);
        final var from = Commands.argument("from", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.INSTANCE);
        final var to = Commands.argument("to", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.INSTANCE);
        return command.create().then(hologramArgument(plugin)
                .then(from.then(to.executes(command))));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var from = context.getArgument("from", int.class);
        final var to = context.getArgument("to", int.class);
        final var success = hologram.moveLine(from - 1, to - 1);
        final var message = success ? "hologram.line.move" : "hologram.line.move.failed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Placeholder.parsed("from", String.valueOf(from)),
                Placeholder.parsed("to", String.valueOf(to)));
        return success ? SINGLE_SUCCESS : 0;
    }
}


