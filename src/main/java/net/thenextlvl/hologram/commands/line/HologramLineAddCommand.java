package net.thenextlvl.hologram.commands.line;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
//  hologram line add block <hologram> <block>
//  hologram line add entity <hologram> <entity>
//  hologram line add item <hologram> <item>
//  hologram line add text <hologram> <text>
public final class HologramLineAddCommand extends BrigadierCommand {
    private HologramLineAddCommand(HologramPlugin plugin) {
        super(plugin, "add", "holograms.command.line.add");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineAddCommand(plugin);
        return command.create()
                .then(Commands.literal("block")
                        .then(hologramArgument(plugin)
                                .then(Commands.argument("block", ArgumentTypes.blockState())
                                        .executes(command::block))))
                .then(Commands.literal("entity")
                        .then(hologramArgument(plugin)
                                .then(Commands.argument("entity", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE))
                                        .executes(command::entity))))
                .then(Commands.literal("item")
                        .then(hologramArgument(plugin)
                                .then(Commands.argument("item", ArgumentTypes.itemStack())
                                        .executes(command::item))))
                .then(Commands.literal("text")
                        .then(hologramArgument(plugin)
                                .then(Commands.argument("text", StringArgumentType.greedyString())
                                        .executes(command::text))));
    }

    private int block(CommandContext<CommandSourceStack> commandContext) {
        var hologram = commandContext.getArgument("hologram", Hologram.class);
        var block = commandContext.getArgument("block", BlockState.class);
        var line = hologram.addBlockLine();
        line.setBlock(block.getBlockData());
        line.spawn();
        return Command.SINGLE_SUCCESS;
    }

    private int entity(CommandContext<CommandSourceStack> commandContext) {
        var hologram = commandContext.getArgument("hologram", Hologram.class);
        var entity = commandContext.getArgument("entity", EntityType.class);
        var line = hologram.addEntityLine(entity);
        line.spawn();
        return Command.SINGLE_SUCCESS;
    }

    private int item(CommandContext<CommandSourceStack> commandContext) {
        var hologram = commandContext.getArgument("hologram", Hologram.class);
        var item = commandContext.getArgument("item", ItemStack.class);
        var line = hologram.addItemLine();
        line.setItemStack(item);
        line.spawn();
        return Command.SINGLE_SUCCESS;
    }

    private int text(CommandContext<CommandSourceStack> commandContext) {
        var hologram = commandContext.getArgument("hologram", Hologram.class);
        var text = commandContext.getArgument("text", String.class);
        var line = hologram.addTextLine();
        line.setText(MiniMessage.miniMessage().deserialize(text));
        line.spawn();
        return Command.SINGLE_SUCCESS;
    }
}
