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
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
final class HologramLineSwapCommand extends SimpleCommand {
    private HologramLineSwapCommand(HologramPlugin plugin) {
        super(plugin, "swap", "holograms.command.line.swap");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineSwapCommand(plugin);
        var first = Commands.argument("first", IntegerArgumentType.integer(1));
        var second = Commands.argument("second", IntegerArgumentType.integer(1));
        return command.create().then(hologramArgument(plugin)
                .then(first.then(second.executes(command))));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var first = context.getArgument("first", int.class);
        var second = context.getArgument("second", int.class);
        var success = hologram.swapLines(hologram.getLineCount() - first, hologram.getLineCount() - second);
        var message = success ? "hologram.line.swap" : "hologram.line.swap.failed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("hologram", hologram.getName()),
                Placeholder.parsed("first", String.valueOf(first)),
                Placeholder.parsed("second", String.valueOf(second)));
        return success ? SINGLE_SUCCESS : 0;
    }
}


