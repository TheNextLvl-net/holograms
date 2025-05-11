package net.thenextlvl.hologram.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.thenextlvl.hologram.HologramPlugin;

class HologramCreateCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        return Commands.literal("create")
                .requires(source -> source.getSender().hasPermission("holograms.command.create"))
                .then(nameArgument().executes(context -> create(context, plugin)));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> nameArgument() {
        return Commands.argument("name", StringArgumentType.string());
    }

    private static int create(CommandContext<CommandSourceStack> context, HologramPlugin plugin) {
        var name = context.getArgument("name", String.class);
        var location = context.getSource().getLocation();
        var hologram = plugin.hologramController().createHologram(name, location);
        hologram.addTextLine().setText(Component.text(""));
        hologram.spawn();
        return Command.SINGLE_SUCCESS;
    }
}
