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
public final class HologramPageSettingsRandomCommand extends BrigadierCommand {
    private HologramPageSettingsRandomCommand(final HologramPlugin plugin) {
        super(plugin, "random", "holograms.command.page.settings.random");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageSettingsRandomCommand(plugin);
        final var random = Commands.argument("random", BoolArgumentType.bool());
        return command.create().then(random.executes(command::setRandom));
    }

    private int setRandom(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineIndex = context.getArgument("line", int.class) - 1;
        final var random = context.getArgument("random", boolean.class);
        final var line = hologram.getLine(lineIndex, PagedHologramLine.class);

        if (line.isEmpty()) {
            plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.type.paged",
                    Placeholder.unparsed("hologram", hologram.getName()),
                    Formatter.number("line", lineIndex + 1));
            return 0;
        }

        line.get().setRandomOrder(random);
        plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.page.random",
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineIndex + 1));
        return SINGLE_SUCCESS;
    }
}
