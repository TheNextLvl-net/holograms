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

@NullMarked
final class HologramActionListCommand extends SimpleCommand {
    private HologramActionListCommand(final HologramPlugin plugin) {
        super(plugin, "list", "holograms.command.action.list");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramActionListCommand(plugin);
        return command.create().then(hologramArgument(plugin)
                .suggests(new HologramWithActionSuggestionProvider<>(plugin))
                .executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var line = hologram.getLine(context.getArgument("line", int.class)).orElseThrow(); // fixme
        final var actions = line.getActions();
        if (actions.isEmpty()) {
            plugin.bundle().sendMessage(sender, "hologram.action.list.empty",
                    Placeholder.unparsed("hologram", hologram.getName()));
            return 0;
        }
        plugin.bundle().sendMessage(sender, "hologram.action.list.header",
                Placeholder.parsed("hologram", hologram.getName()));
        actions.forEach((name, action) -> plugin.bundle().sendMessage(sender, "hologram.action.list",
                Placeholder.parsed("action_type", action.getActionType().name()),
                Placeholder.parsed("hologram", hologram.getName()),
                Placeholder.parsed("action", name)));
        return SINGLE_SUCCESS;
    }
}
