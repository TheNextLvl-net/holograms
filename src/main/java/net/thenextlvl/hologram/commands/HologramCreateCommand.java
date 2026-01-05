package net.thenextlvl.hologram.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;

import static net.thenextlvl.hologram.commands.HologramCommand.nameArgument;

@NullMarked
final class HologramCreateCommand extends SimpleCommand {
    private HologramCreateCommand(HologramPlugin plugin) {
        super(plugin, "create", "holograms.command.create");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramCreateCommand(plugin);
        return command.create().then(nameArgument().executes(command));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var name = context.getArgument("name", String.class);
        var location = context.getSource().getLocation();
        var sender = context.getSource().getSender();

        var placeholder = Placeholder.parsed("hologram", name);
        if (plugin.hologramProvider().hasHologram(name)) {
            plugin.bundle().sendMessage(sender, "hologram.exists", placeholder);
            return 0;
        }

        plugin.hologramProvider().spawnHologram(name, location, hologram -> {
            var text = plugin.bundle().component("hologram.default", Locale.US,
                    Placeholder.parsed("hologram", StringArgumentType.escapeIfRequired(name)));
            hologram.addTextLine().setText(text).setLineWidth(220);
        });

        plugin.bundle().sendMessage(sender, "hologram.created", placeholder);
        return SINGLE_SUCCESS;
    }
}
