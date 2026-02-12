package net.thenextlvl.hologram.commands.line;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.commands.suggestions.LineSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;
import static net.thenextlvl.hologram.commands.HologramCommand.permissionArgument;

@NullMarked
final class HologramLineViewPermissionCommand extends SimpleCommand {
    private HologramLineViewPermissionCommand(final HologramPlugin plugin) {
        super(plugin, "view-permission", "holograms.command.line.view-permission");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineViewPermissionCommand(plugin);
        final var line = Commands.argument("line", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.ANY_LINE);
        return command.create().then(hologramArgument(plugin)
                .then(line
                        .then(Commands.literal("remove").executes(command::set))
                        .then(permissionArgument(plugin).executes(command::set))
                        .executes(command)));
    }

    private int set(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineIndex = context.getArgument("line", int.class);
        final var hologramLine = hologram.getLine(lineIndex - 1).orElse(null);
        if (hologramLine == null) {
            plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.line.invalid");
            return 0;
        }
        final var viewPermission = tryGetArgument(context, "permission", String.class).orElse(null);
        final var success = hologramLine.setViewPermission(viewPermission);
        final var message = !success ? "nothing.changed" : viewPermission != null
                ? "hologram.line.view-permission.set" : "hologram.line.view-permission.removed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineIndex),
                Placeholder.unparsed("permission", String.valueOf(viewPermission)));
        return success ? SINGLE_SUCCESS : 0;
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineIndex = context.getArgument("line", int.class);
        final var hologramLine = hologram.getLine(lineIndex - 1).orElse(null);
        if (hologramLine == null) {
            plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.line.invalid");
            return 0;
        }
        final var permission = hologramLine.getViewPermission().orElse(null);
        final var message = permission != null ? "hologram.line.view-permission" : "hologram.line.view-permission.none";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineIndex),
                Placeholder.unparsed("permission", String.valueOf(permission)));
        return SINGLE_SUCCESS;
    }
}
