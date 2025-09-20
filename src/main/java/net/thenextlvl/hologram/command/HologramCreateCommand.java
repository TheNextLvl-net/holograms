package net.thenextlvl.hologram.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.command.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;

@NullMarked
final class HologramCreateCommand extends SimpleCommand {
    private HologramCreateCommand(HologramPlugin plugin) {
        super(plugin, "create", "holograms.command.create");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramCreateCommand(plugin);
        return command.create().then(nameArgument().executes(command));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> nameArgument() {
        return Commands.argument("name", StringArgumentType.string());
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var name = context.getArgument("name", String.class);
        var location = context.getSource().getLocation();
        var sender = context.getSource().getSender();

        var placeholder = Placeholder.parsed("hologram", name);
        if (plugin.hologramController().hologramExists(name)) {
            plugin.bundle().sendMessage(sender, "hologram.exists", placeholder);
            return 0;
        }

        plugin.hologramController().spawnHologram(name, location, hologram -> {
            hologram.addTextLine().setText(plugin.bundle().component("hologram.default", Locale.US,
                    Placeholder.parsed("hologram", StringArgumentType.escapeIfRequired(name))));
        });

        plugin.bundle().sendMessage(sender, "hologram.created", placeholder);
        return SINGLE_SUCCESS;
    }
}
