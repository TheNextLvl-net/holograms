package net.thenextlvl.hologram.commands.action;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.commands.suggestions.HologramWithActionSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;
import static net.thenextlvl.hologram.commands.action.HologramActionCommand.actionArgument;

@NullMarked
final class HologramActionRemoveCommand extends SimpleCommand {
    private HologramActionRemoveCommand(final HologramPlugin plugin) {
        super(plugin, "remove", "holograms.command.action.remove");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramActionRemoveCommand(plugin);
        return command.create().then(hologramArgument(plugin)
                .suggests(new HologramWithActionSuggestionProvider<>(plugin))
                .then(actionArgument(plugin).executes(command)));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var line = hologram.getLine(context.getArgument("line", int.class)).orElseThrow(); // fixme
        final var action = context.getArgument("action", String.class);

        final var success = line.removeAction(action);
        final var message = success ? "hologram.action.removed" : "hologram.action.not_found";

        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("hologram", hologram.getName()),
                Placeholder.parsed("action", action));
        return success ? SINGLE_SUCCESS : 0;
    }
}
