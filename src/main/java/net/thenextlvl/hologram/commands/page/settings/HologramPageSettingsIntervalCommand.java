package net.thenextlvl.hologram.commands.page.settings;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.util.Tick;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class HologramPageSettingsIntervalCommand extends SimpleCommand {
    private HologramPageSettingsIntervalCommand(final HologramPlugin plugin) {
        super(plugin, "interval", "holograms.command.page.settings.interval");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageSettingsIntervalCommand(plugin);
        final var interval = Commands.argument("interval", ArgumentTypes.time(1));
        return command.create().then(interval.executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineIndex = context.getArgument("line", int.class) - 1;
        final var interval = Tick.of(context.getArgument("interval", int.class));
        final var line = hologram.getLine(lineIndex, PagedHologramLine.class).orElse(null);

        if (line == null) {
            plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.type.paged",
                    Placeholder.unparsed("hologram", hologram.getName()),
                    Formatter.number("line", lineIndex + 1));
            return 0;
        }

        final var success = !line.getInterval().equals(interval);
        if (success) line.setInterval(interval);
        final var message = success ? "hologram.page.interval.success" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineIndex + 1));
        return success ? SINGLE_SUCCESS : 0;
    }
}
