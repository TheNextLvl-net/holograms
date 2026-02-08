package net.thenextlvl.hologram.commands.action;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramActionListCommand extends SimpleCommand {
    private final ActionTargetResolver.Builder resolverBuilder;

    private HologramActionListCommand(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        super(plugin, "list", "holograms.command.action.list");
        this.resolverBuilder = resolver;
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        final var command = new HologramActionListCommand(plugin, resolver);
        return command.create().executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return resolverBuilder.build(context, plugin).resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var sender = context.getSource().getSender();
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
        });
    }
}
