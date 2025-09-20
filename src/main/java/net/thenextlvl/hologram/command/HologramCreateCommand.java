package net.thenextlvl.hologram.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.command.brigadier.SimpleCommand;

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
        var hologram = plugin.hologramController().createHologram(name, location);
        hologram.addTextLine().setText(Component.text(name).appendNewline()
                .append(Component.text("Use '/hologram line add " + name + " <name> <text>' to add a new line")).appendNewline()
                .append(Component.text("Use '/hologram delete " + name + "' to remove the hologram")));
        hologram.spawn();
        return SINGLE_SUCCESS;
    }
}
