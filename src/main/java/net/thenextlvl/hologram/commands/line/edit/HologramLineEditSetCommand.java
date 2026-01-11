package net.thenextlvl.hologram.commands.line.edit;

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
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiConsumer;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@NullMarked
final class HologramLineEditSetCommand extends BrigadierCommand {
    private HologramLineEditSetCommand(HologramPlugin plugin) {
        super(plugin, "set", "holograms.command.line.edit.set");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditSetCommand(plugin);
        return command.create()
                .then(command.setLine("block", ArgumentTypes.blockState(), command::setBlockLine))
                .then(command.setLine("entity", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE), command::setEntityLine))
                .then(command.setLine("item", ArgumentTypes.itemStack(), command::setItemLine))
                .then(command.setLine("text", StringArgumentType.greedyString(), command::setTextLine));
    }

    private LiteralArgumentBuilder<CommandSourceStack> setLine(String name, ArgumentType<?> argumentType, Command<CommandSourceStack> command) {
        return Commands.literal(name).then(Commands.argument(name, argumentType).executes(command));
    }

    private int setBlockLine(CommandContext<CommandSourceStack> context) {
        var block = context.getArgument("block", BlockState.class).getBlockData();
        return setLine(context, (hologram, line) -> {
            hologram.getLine(line, BlockHologramLine.class)
                    .orElseGet(() -> hologram.setBlockLine(line))
                    .setBlock(block);
        });
    }

    private int setEntityLine(CommandContext<CommandSourceStack> context) {
        var entity = context.getArgument("entity", EntityType.class);
        return setLine(context, (hologram, line) -> hologram.setEntityLine(entity, line));
    }

    private int setItemLine(CommandContext<CommandSourceStack> context) {
        var item = context.getArgument("item", ItemStack.class);
        return setLine(context, (hologram, line) -> {
            hologram.getLine(line, ItemHologramLine.class)
                    .orElseGet(() -> hologram.setItemLine(line))
                    .setItemStack(item);
        });
    }

    private int setTextLine(CommandContext<CommandSourceStack> context) {
        var text = MiniMessage.miniMessage().deserialize(context.getArgument("text", String.class));
        return setLine(context, (hologram, line) -> {
            hologram.getLine(line, TextHologramLine.class)
                    .orElseGet(() -> hologram.setTextLine(line))
                    .setText(text);
        });
    }

    private int setLine(CommandContext<CommandSourceStack> context, BiConsumer<Hologram, Integer> setter) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var line = context.getArgument("line", int.class) - 1;
        setter.accept(hologram, line);
        // todo: send message
        return SINGLE_SUCCESS;
    }
}
