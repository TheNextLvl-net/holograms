package net.thenextlvl.hologram.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.nameArgument;

@NullMarked
final class HologramCreateCommand extends SimpleCommand {
    private HologramCreateCommand(final HologramPlugin plugin) {
        super(plugin, "create", "holograms.command.create");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramCreateCommand(plugin);
        final var position = Commands.argument("position", ArgumentTypes.finePosition()).executes(command);
        final var world = Commands.argument("world", ArgumentTypes.world()).executes(command);
        return command.create().then(nameArgument().then(position.then(world)).executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var name = context.getArgument("name", String.class);
        final var world = tryGetArgument(context, "world", World.class)
                .orElseGet(() -> context.getSource().getLocation().getWorld());
        final var location = resolveArgument(context, "position", FinePositionResolver.class)
                .map(finePosition -> finePosition.toLocation(world))
                .orElseGet(context.getSource()::getLocation);
        final var sender = context.getSource().getSender();

        final var placeholder = Placeholder.parsed("hologram", name);
        if (plugin.hologramProvider().hasHologram(name)) {
            plugin.bundle().sendMessage(sender, "hologram.exists", placeholder);
            return 0;
        }

        plugin.hologramProvider().spawnHologram(name, location, hologram -> {
            hologram.addTextLine().setUnparsedText("<lang:hologram.default>"); // todo: move translation from bundle to store
        });

        plugin.bundle().sendMessage(sender, "hologram.created", placeholder);
        return SINGLE_SUCCESS;
    }
}
