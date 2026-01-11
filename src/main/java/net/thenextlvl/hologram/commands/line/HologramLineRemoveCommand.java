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
final class HologramLineRemoveCommand extends SimpleCommand {
    private HologramLineRemoveCommand(HologramPlugin plugin) {
        super(plugin, "remove", "holograms.command.line.remove");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineRemoveCommand(plugin);
        var line = Commands.argument("line", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.INSTANCE);
        return command.create().then(hologramArgument(plugin)
                .then(line.executes(command)));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var line = context.getArgument("line", int.class);
        var success = hologram.removeLine(line - 1);
        var message = success ? "hologram.line.remove" : "hologram.line.remove.failed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("hologram", hologram.getName()),
                Placeholder.parsed("line", String.valueOf(line)));
        return success ? SINGLE_SUCCESS : 0;
    }
}
