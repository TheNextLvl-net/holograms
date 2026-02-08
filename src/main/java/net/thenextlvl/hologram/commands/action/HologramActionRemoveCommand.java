package net.thenextlvl.hologram.commands.action;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.action.ActionCommand.actionArgument;

@NullMarked
final class HologramActionRemoveCommand extends SimpleCommand {
    private final ActionTargetResolver.Builder resolverBuilder;

    private HologramActionRemoveCommand(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        super(plugin, "remove", "holograms.command.action.remove");
        this.resolverBuilder = resolver;
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        final var command = new HologramActionRemoveCommand(plugin, resolver);
        return command.create().then(actionArgument(plugin).executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return resolverBuilder.build(context, plugin).resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var sender = context.getSource().getSender();
            final var action = context.getArgument("action", String.class);

            final var success = line.removeAction(action);
            final var message = success ? "hologram.action.removed" : "hologram.action.not_found";

            plugin.bundle().sendMessage(sender, message,
                    Placeholder.parsed("hologram", hologram.getName()),
                    Placeholder.parsed("action", action));
            return success ? SINGLE_SUCCESS : 0;
        });
    }
}
