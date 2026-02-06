package net.thenextlvl.hologram.commands.action;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.commands.suggestions.HologramWithActionSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;
import static net.thenextlvl.hologram.commands.action.HologramActionCommand.actionArgument;

@NullMarked
final class HologramActionChanceCommand extends ActionCommand {
    private HologramActionChanceCommand(final HologramPlugin plugin) {
        super(plugin, "chance", "holograms.command.action.chance");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramActionChanceCommand(plugin);
        return command.create().then(hologramArgument(plugin)
                .suggests(new HologramWithActionSuggestionProvider<>(plugin))
                .then(actionArgument(plugin)
                        .then(chanceArgument().executes(command))
                        .executes(command)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> chanceArgument() {
        return Commands.argument("chance", IntegerArgumentType.integer(0, 100));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final Hologram hologram, final ClickAction<?> action, final String actionName) {
        final var chance = tryGetArgument(context, "chance", int.class).orElse(null);
        final var success = chance != null && action.setChance(chance);
        final var message = chance == null ? "hologram.action.chance"
                : success ? "hologram.action.chance.set" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("chance", chance != null ? chance : action.getChance()));
        return success ? SINGLE_SUCCESS : 0;
    }
}
