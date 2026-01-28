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
final class HologramLineSwapCommand extends SimpleCommand {
    private HologramLineSwapCommand(final HologramPlugin plugin) {
        super(plugin, "swap", "holograms.command.line.swap");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineSwapCommand(plugin);
        final var first = Commands.argument("first", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.ANY_LINE);
        final var second = Commands.argument("second", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.ANY_LINE);
        return command.create().then(hologramArgument(plugin)
                .then(first.then(second.executes(command))));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var first = context.getArgument("first", int.class);
        final var second = context.getArgument("second", int.class);
        final var success = hologram.swapLines(first - 1, second - 1);
        final var message = success ? "hologram.line.swap" : "hologram.line.swap.failed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Placeholder.parsed("first", String.valueOf(first)),
                Placeholder.parsed("second", String.valueOf(second)));
        return success ? SINGLE_SUCCESS : 0;
    }
}


