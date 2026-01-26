package net.thenextlvl.hologram.commands.page.edit;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@NullMarked
public final class HologramPageEditPauseCommand extends BrigadierCommand {
    private HologramPageEditPauseCommand(final HologramPlugin plugin) {
        super(plugin, "pause", "holograms.command.page.edit.pause");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageEditPauseCommand(plugin);
        return command.create()
                .then(Commands.argument("paused", BoolArgumentType.bool())
                        .executes(command::setPaused));
    }

    private int setPaused(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineIndex = context.getArgument("line", int.class) - 1;
        final var paused = context.getArgument("paused", boolean.class);
        final var line = hologram.getLine(lineIndex, PagedHologramLine.class);

        if (line.isEmpty()) {
            plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.type.paged");
            return 0;
        }

        line.get().setPaused(paused);
        plugin.bundle().sendMessage(context.getSource().getSender(),
                paused ? "hologram.page.paused" : "hologram.page.resumed",
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineIndex + 1));
        return SINGLE_SUCCESS;
    }
}
