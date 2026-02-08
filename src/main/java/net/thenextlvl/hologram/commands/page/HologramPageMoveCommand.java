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

@NullMarked
public final class HologramPageMoveCommand extends SimpleCommand {
    private HologramPageMoveCommand(final HologramPlugin plugin) {
        super(plugin, "move", "holograms.command.page.move");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageMoveCommand(plugin);
        final var line = Commands.argument("line", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.PAGED_ONLY);
        final var from = Commands.argument("from", IntegerArgumentType.integer(1))
                .suggests(PageSuggestionProvider.INSTANCE);
        final var to = Commands.argument("to", IntegerArgumentType.integer(1))
                .suggests(PageSuggestionProvider.INSTANCE);
        return command.create().then(hologramArgument(plugin, true).then(line
                .then(from.then(to.executes(command)))));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineIndex = context.getArgument("line", int.class) - 1;
        final var from = context.getArgument("from", int.class);
        final var to = context.getArgument("to", int.class);

        final var line = hologram.getLine(lineIndex).orElse(null);
        if (line == null) {
            plugin.bundle().sendMessage(sender, "hologram.line.invalid");
            return 0;
        }
        if (!(line instanceof final PagedHologramLine pagedLine)) {
            plugin.bundle().sendMessage(sender, "hologram.type.paged");
            return 0;
        }

        final var success = pagedLine.movePage(from - 1, to - 1);
        final var message = success ? "hologram.page.move" : "hologram.page.move.failed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineIndex + 1),
                Formatter.number("from", from),
                Formatter.number("to", to));
        return success ? SINGLE_SUCCESS : 0;
    }
}
