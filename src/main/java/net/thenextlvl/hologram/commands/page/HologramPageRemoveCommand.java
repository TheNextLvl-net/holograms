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
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.commands.suggestions.LineSuggestionProvider;
import net.thenextlvl.hologram.commands.suggestions.PageSuggestionProvider;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
public final class HologramPageRemoveCommand extends BrigadierCommand {
    private HologramPageRemoveCommand(final HologramPlugin plugin) {
        super(plugin, "remove", "holograms.command.page.remove");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageRemoveCommand(plugin);
        final var line = Commands.argument("line", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.PAGED_ONLY);
        final var page = Commands.argument("page", IntegerArgumentType.integer(1))
                .suggests(PageSuggestionProvider.INSTANCE);
        return command.create().then(hologramArgument(plugin, true).then(line.then(page
                .executes(command::removePage))));
    }

    private int removePage(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineIndex = context.getArgument("line", int.class) - 1;
        final var pageIndex = context.getArgument("page", int.class) - 1;
        final var line = hologram.getLine(lineIndex).orElse(null);

        if (line == null) {
            plugin.bundle().sendMessage(sender, "hologram.line.invalid");
            return 0;
        }
        if (!(line instanceof final PagedHologramLine pagedLine)) {
            plugin.bundle().sendMessage(sender, "hologram.type.paged");
            return 0;
        }

        final var removed = pagedLine.removePage(pageIndex);

        plugin.bundle().sendMessage(sender,
                removed ? "hologram.page.remove" : "hologram.line.invalid",
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineIndex + 1),
                Formatter.number("page", pageIndex + 1));
        return removed ? SINGLE_SUCCESS : 0;
    }
}
