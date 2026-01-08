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

import java.util.function.Consumer;

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
                .then(command.addLine("block", ArgumentTypes.blockState(), command::addBlockLine))
                .then(command.addLine("entity", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE), command::addEntityLine))
                .then(command.addLine("item", ArgumentTypes.itemStack(), command::addItemLine))
                .then(command.addLine("text", StringArgumentType.greedyString(), command::addTextLine)));
    }

    private LiteralArgumentBuilder<CommandSourceStack> addLine(String name, ArgumentType<?> argumentType, Command<CommandSourceStack> command) {
        return Commands.literal(name).then(Commands.argument(name, argumentType).executes(command));
    }

    private int addBlockLine(CommandContext<CommandSourceStack> context) {
        var block = context.getArgument("block", BlockState.class).getBlockData();
        return addLine(context, hologram -> hologram.addBlockLine().setBlock(block));
    }

    private int addEntityLine(CommandContext<CommandSourceStack> context) {
        var entity = context.getArgument("entity", EntityType.class);
        return addLine(context, hologram -> hologram.addEntityLine(entity));
    }

    private int addItemLine(CommandContext<CommandSourceStack> context) {
        var item = context.getArgument("item", ItemStack.class);
        return addLine(context, hologram -> hologram.addItemLine().setItemStack(item));
    }

    private int addTextLine(CommandContext<CommandSourceStack> context) {
        var text = MiniMessage.miniMessage().deserialize(context.getArgument("text", String.class));
        return addLine(context, hologram -> hologram.addTextLine().setText(text));
    }

    private int addLine(CommandContext<CommandSourceStack> context, Consumer<Hologram> consumer) {
        var hologram = context.getArgument("hologram", Hologram.class);
        consumer.accept(hologram);
        // todo: send message
        return SINGLE_SUCCESS;
    }
}
