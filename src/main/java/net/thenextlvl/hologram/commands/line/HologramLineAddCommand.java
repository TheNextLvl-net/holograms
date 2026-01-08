package net.thenextlvl.hologram.commands.line;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
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

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
public final class HologramLineAddCommand extends BrigadierCommand {
    private HologramLineAddCommand(HologramPlugin plugin) {
        super(plugin, "add", "holograms.command.line.add");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineAddCommand(plugin);
        return command.create().then(hologramArgument(plugin)
                .then(command.createLine("block", ArgumentTypes.blockState(), command::block))
                .then(command.createLine("entity", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE), command::entity))
                .then(command.createLine("item", ArgumentTypes.itemStack(), command::item))
                .then(command.createLine("text", StringArgumentType.greedyString(), command::text)));
    }

    private LiteralArgumentBuilder<CommandSourceStack> createLine(String name, ArgumentType<?> argumentType, Command<CommandSourceStack> command) {
        return Commands.literal(name).then(Commands.argument(name, argumentType).executes(command));
    }

    private int block(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var block = context.getArgument("block", BlockState.class);
        hologram.addBlockLine().setBlock(block.getBlockData());
        return SINGLE_SUCCESS;
    }

    private int entity(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var entity = context.getArgument("entity", EntityType.class);
        hologram.addEntityLine(entity);
        return SINGLE_SUCCESS;
    }

    private int item(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var item = context.getArgument("item", ItemStack.class);
        hologram.addItemLine().setItemStack(item);
        return SINGLE_SUCCESS;
    }

    private int text(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var text = context.getArgument("text", String.class);
        hologram.addTextLine().setText(MiniMessage.miniMessage().deserialize(text));
        return SINGLE_SUCCESS;
    }
}
