package net.thenextlvl.hologram.commands.page.settings;

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
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@NullMarked
public final class HologramPageSettingsIntervalCommand extends BrigadierCommand {
    private HologramPageSettingsIntervalCommand(final HologramPlugin plugin) {
        super(plugin, "interval", "holograms.command.page.settings.interval");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageSettingsIntervalCommand(plugin);
        final var ticks = Commands.argument("ticks", IntegerArgumentType.integer(1));
        return command.create().then(ticks.executes(command::setInterval));
    }

    private int setInterval(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineIndex = context.getArgument("line", int.class) - 1;
        final var ticks = context.getArgument("ticks", int.class);
        final var line = hologram.getLine(lineIndex, PagedHologramLine.class);

        if (line.isEmpty()) {
            plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.type.paged",
                    Placeholder.unparsed("hologram", hologram.getName()),
                    Formatter.number("line", lineIndex + 1));
            return 0;
        }

        line.get().setInterval(Duration.ofMillis(ticks * 50L));
        plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.page.interval",
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineIndex + 1));
        return SINGLE_SUCCESS;
    }
}
