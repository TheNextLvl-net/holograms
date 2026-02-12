package net.thenextlvl.hologram.commands.page;

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
import net.thenextlvl.hologram.commands.suggestions.PageSuggestionProvider;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;
import static net.thenextlvl.hologram.commands.HologramCommand.permissionArgument;

@NullMarked
final class HologramPageViewPermissionCommand extends SimpleCommand {
    private HologramPageViewPermissionCommand(final HologramPlugin plugin) {
        super(plugin, "view-permission", "holograms.command.page.view-permission");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageViewPermissionCommand(plugin);
        final var line = Commands.argument("line", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.PAGED_ONLY);
        final var page = Commands.argument("page", IntegerArgumentType.integer(1))
                .suggests(PageSuggestionProvider.INSTANCE);
        return command.create().then(hologramArgument(plugin, true)
                .then(line.then(page
                        .then(Commands.literal("remove").executes(command::set))
                        .then(permissionArgument(plugin).executes(command::set))
                        .executes(command))));
    }

    private int set(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineIndex = context.getArgument("line", int.class);
        final var pageIndex = context.getArgument("page", int.class);
        final var line = hologram.getLine(lineIndex - 1).orElse(null);

        if (line == null) {
            plugin.bundle().sendMessage(sender, "hologram.line.invalid");
            return 0;
        }
        if (!(line instanceof final PagedHologramLine pagedLine)) {
            plugin.bundle().sendMessage(sender, "hologram.type.paged");
            return 0;
        }

        final var hologramPage = pagedLine.getPage(pageIndex - 1).orElse(null);
        if (hologramPage == null) {
            plugin.bundle().sendMessage(sender, "hologram.page.invalid");
            return 0;
        }

        final var viewPermission = tryGetArgument(context, "permission", String.class).orElse(null);
        final var success = hologramPage.setViewPermission(viewPermission);
        final var message = !success ? "nothing.changed" : viewPermission != null
                ? "hologram.page.view-permission.set" : "hologram.page.view-permission.removed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineIndex),
                Formatter.number("page", pageIndex),
                Placeholder.unparsed("permission", String.valueOf(viewPermission)));
        return success ? SINGLE_SUCCESS : 0;
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineIndex = context.getArgument("line", int.class);
        final var pageIndex = context.getArgument("page", int.class);
        final var line = hologram.getLine(lineIndex - 1).orElse(null);

        if (line == null) {
            plugin.bundle().sendMessage(sender, "hologram.line.invalid");
            return 0;
        }
        if (!(line instanceof final PagedHologramLine pagedLine)) {
            plugin.bundle().sendMessage(sender, "hologram.type.paged");
            return 0;
        }

        final var hologramPage = pagedLine.getPage(pageIndex - 1).orElse(null);
        if (hologramPage == null) {
            plugin.bundle().sendMessage(sender, "hologram.page.invalid");
            return 0;
        }

        final var permission = hologramPage.getViewPermission().orElse(null);
        final var message = permission != null ? "hologram.page.view-permission" : "hologram.page.view-permission.none";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineIndex),
                Formatter.number("page", pageIndex),
                Placeholder.unparsed("permission", String.valueOf(permission)));
        return SINGLE_SUCCESS;
    }
}
