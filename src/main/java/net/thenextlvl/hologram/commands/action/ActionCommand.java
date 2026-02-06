package net.thenextlvl.hologram.commands.action;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
abstract class ActionCommand extends SimpleCommand {
    protected ActionCommand(final HologramPlugin plugin, final String name, @Nullable final String permission) {
        super(plugin, name, permission);
    }

    @Override
    public final int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var sender = context.getSource().getSender();
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var actionName = context.getArgument("action", String.class);
        final var line = hologram.getLine(context.getArgument("line", int.class)).orElseThrow(); // fixme
        final var action = line.getAction(actionName).orElse(null);
        if (action == null) {
            plugin.bundle().sendMessage(sender, "hologram.action.not_found",
                    Placeholder.parsed("hologram", hologram.getName()),
                    Placeholder.unparsed("action", actionName));
            return 0;
        }
        return run(context, hologram, action, actionName);
    }

    public abstract int run(CommandContext<CommandSourceStack> context, Hologram hologram, ClickAction<?> action, String actionName) throws CommandSyntaxException;
}
