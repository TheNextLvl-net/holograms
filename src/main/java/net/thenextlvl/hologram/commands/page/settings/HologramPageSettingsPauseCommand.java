package net.thenextlvl.hologram.commands.page.settings;

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
public final class HologramPageSettingsPauseCommand extends BrigadierCommand {
    private HologramPageSettingsPauseCommand(final HologramPlugin plugin) {
        super(plugin, "pause", "holograms.command.page.settings.pause");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageSettingsPauseCommand(plugin);
        final var paused = Commands.argument("paused", BoolArgumentType.bool());
        return command.create().then(paused.executes(command::setPause));
    }

    private int setPause(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineIndex = context.getArgument("line", int.class) - 1;
        final var paused = context.getArgument("paused", boolean.class);
        final var line = hologram.getLine(lineIndex, PagedHologramLine.class);

        if (line.isEmpty()) {
            plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.type.paged",
                    Placeholder.unparsed("hologram", hologram.getName()),
                    Formatter.number("line", lineIndex + 1));
            return 0;
        }

        line.get().setPaused(paused);
        plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.page.pause",
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineIndex + 1));
        return SINGLE_SUCCESS;
    }
}
