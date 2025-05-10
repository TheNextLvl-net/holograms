package net.thenextlvl.hologram.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;

import java.util.function.Function;

class HologramCreateCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        return Commands.literal("create")
                .requires(source -> source.getSender().hasPermission("holograms.command.create"))
                .then(block(plugin))
                .then(item(plugin))
                .then(text(plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> block(HologramPlugin plugin) {
        return Commands.literal("block").then(nameArgument().executes(context ->
                create(context, plugin.hologramController()::createBlockHologram, plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> item(HologramPlugin plugin) {
        return Commands.literal("item").then(nameArgument().executes(context ->
                create(context, plugin.hologramController()::createItemHologram, plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> text(HologramPlugin plugin) {
        return Commands.literal("text").then(nameArgument().executes(context ->
                create(context, plugin.hologramController()::createTextHologram, plugin)));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> nameArgument() {
        return Commands.argument("name", StringArgumentType.string());
    }

    private static <T extends Hologram<?>> int create(CommandContext<CommandSourceStack> context, Function<String, T> creator, HologramPlugin plugin) {
        var name = context.getArgument("name", String.class);
        var hologram = creator.apply(name);
        return Command.SINGLE_SUCCESS;
    }
}
