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
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
public final class HologramPageClearCommand extends BrigadierCommand {
    private HologramPageClearCommand(final HologramPlugin plugin) {
        super(plugin, "clear", "holograms.command.page.clear");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageClearCommand(plugin);
        final var line = Commands.argument("line", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.PAGED_ONLY);
        return command.create().then(hologramArgument(plugin, true).then(line
                .executes(command::clearPages)));
    }

    private int clearPages(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineIndex = context.getArgument("line", int.class) - 1;
        final var line = hologram.getLine(lineIndex, PagedHologramLine.class);

        if (line.isEmpty()) {
            plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.type.paged");
            return 0;
        }

        line.get().clearPages();
        plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.page.clear",
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineIndex + 1));
        return SINGLE_SUCCESS;
    }
}
