package net.thenextlvl.hologram.commands.action;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.line.HologramLine;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.permissionArgument;

@NullMarked
final class ActionPermissionCommand extends ActionCommand {
    private ActionPermissionCommand(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        super(plugin, "permission", "holograms.command.action.permission", resolver);
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        final var command = new ActionPermissionCommand(plugin, resolver);
        return command.create().then(actionArgument(plugin)
                .then(Commands.literal("remove").executes(command::set))
                .then(permissionArgument(plugin).executes(command::set))
                .executes(command));
    }

    private int set(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return resolverBuilder.build(context, plugin).resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var sender = context.getSource().getSender();
            final var actionName = context.getArgument("action", String.class);
            final var action = line.getAction(actionName).orElse(null);
            final var permission = tryGetArgument(context, "permission", String.class).orElse(null);

            if (action == null) {
                plugin.bundle().sendMessage(sender, "hologram.action.not_found",
                        Placeholder.parsed("hologram", hologram.getName()),
                        Placeholder.unparsed("action", actionName));
                return 0;
            }

            final var success = action.setPermission(permission);
            final var message = success ? permission != null
                    ? "hologram.action.permission.set" : "hologram.action.permission.removed"
                    : "nothing.changed";
            plugin.bundle().sendMessage(sender, message,
                    Placeholder.unparsed("action", actionName),
                    Placeholder.unparsed("hologram", hologram.getName()),
                    Placeholder.unparsed("permission", String.valueOf(permission)));
            return success ? SINGLE_SUCCESS : 0;
        });
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final Hologram hologram, final HologramLine line, final ClickAction<?> action, final String actionName, final TagResolver... placeholders) {
        final var message = action.getPermission() != null ? "hologram.action.permission" : "hologram.action.permission.none";
        plugin.bundle().sendMessage(context.getSource().getSender(), message, concat(placeholders,
                Placeholder.unparsed("permission", String.valueOf(action.getPermission())),
                Placeholder.unparsed("action", actionName)));
        return SINGLE_SUCCESS;
    }
}
