package net.thenextlvl.hologram.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NullMarked
final class HologramListCommand extends SimpleCommand {
    private static final Comparator<Hologram> HOLOGRAM_COMPARATOR = Comparator.comparing(
            Hologram::getName, String.CASE_INSENSITIVE_ORDER
    );
    private static final Comparator<Map.Entry<World, ? extends List<Hologram>>> WORLD_COMPARATOR = Comparator
            .<Map.Entry<World, ? extends List<Hologram>>>comparingInt(entry -> entry.getValue().size())
            .reversed()
            .thenComparing(entry -> entry.getKey().key().asString(), String.CASE_INSENSITIVE_ORDER);

    private HologramListCommand(final HologramPlugin plugin) {
        super(plugin, "list", "holograms.command.list");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramListCommand(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> commandContext) {
        final var sender = commandContext.getSource().getSender();
        final var worlds = plugin.hologramProvider().getHolograms()
                .sorted(HOLOGRAM_COMPARATOR)
                .collect(Collectors.groupingBy(Hologram::getWorld))
                .entrySet().stream()
                .sorted(WORLD_COMPARATOR)
                .toList();

        final var amount = worlds.stream().mapToInt(entry -> entry.getValue().size()).sum();
        if (amount == 0) {
            plugin.bundle().sendMessage(sender, "hologram.list.empty");
            return 0;
        }

        plugin.bundle().sendMessage(sender, "hologram.list.header",
                Formatter.booleanChoice("hologram_plural", amount != 1),
                Formatter.booleanChoice("world_plural", worlds.size() != 1),
                Formatter.number("amount", amount),
                Formatter.number("worlds", worlds.size()));

        for (final var entry : worlds) {
            final var world = entry.getKey();
            final var holograms = entry.getValue();

            plugin.bundle().sendMessage(sender, "hologram.list.world",
                    Placeholder.parsed("world", world.key().asString()),
                    Formatter.number("amount", holograms.size()));

            for (var hologramIndex = 0; hologramIndex < holograms.size(); hologramIndex++) {
                final var hologram = holograms.get(hologramIndex);
                plugin.bundle().sendMessage(sender, "hologram.list.entry",
                        Placeholder.parsed("tree", hologramIndex + 1 == holograms.size() ? "└" : "├"),
                        Placeholder.unparsed("hologram", hologram.getName()),
                        Placeholder.parsed("command", "/hologram info " + StringArgumentType.escapeIfRequired(hologram.getName())));
            }
        }
        return SINGLE_SUCCESS;
    }
}
