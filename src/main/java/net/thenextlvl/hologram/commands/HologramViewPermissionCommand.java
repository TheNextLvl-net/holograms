package net.thenextlvl.hologram.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.commands.suggestions.PermissionSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
final class HologramViewPermissionCommand extends SimpleCommand {
    private HologramViewPermissionCommand(final HologramPlugin plugin) {
        super(plugin, "view-permission", "holograms.command.view-permission");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramViewPermissionCommand(plugin);
        final var permission = Commands.argument("permission", StringArgumentType.string())
                .suggests(new PermissionSuggestionProvider<>(plugin));
        return command.create().then(hologramArgument(plugin)
                .then(Commands.literal("remove").executes(command::set))
                .then(permission.executes(command::set))
                .executes(command));
    }

    private int set(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var viewPermission = tryGetArgument(context, "permission", String.class).orElse(null);
        final var success = hologram.setViewPermission(viewPermission);
        final var message = !success ? "nothing.changed" : viewPermission != null
                ? "hologram.view-permission.set" : "hologram.view-permission.removed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Placeholder.unparsed("permission", String.valueOf(viewPermission)));
        return success ? SINGLE_SUCCESS : 0;
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var permission = hologram.getViewPermission().orElse(null);
        final var message = permission != null ? "hologram.view-permission" : "hologram.view-permission.none";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Placeholder.unparsed("permission", String.valueOf(permission)));
        return SINGLE_SUCCESS;
    }
}
