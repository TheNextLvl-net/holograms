package net.thenextlvl.hologram.commands.line;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import net.thenextlvl.hologram.commands.suggestions.LineSuggestionProvider;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiConsumer;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
public final class HologramLineInsertCommand extends BrigadierCommand {
    private HologramLineInsertCommand(HologramPlugin plugin) {
        super(plugin, "insert", "holograms.command.line.insert");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineInsertCommand(plugin);
        var line = Commands.argument("line", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.INSTANCE);
        return command.create().then(hologramArgument(plugin).then(line
                .then(command.createLine("block", ArgumentTypes.blockState(), command::insertBlockLine))
                .then(command.createLine("entity", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE), command::insertEntityLine))
                .then(command.createLine("item", ArgumentTypes.itemStack(), command::insertItemLine))
                .then(command.createLine("text", StringArgumentType.greedyString(), command::insertTextLine))));
    }

    private LiteralArgumentBuilder<CommandSourceStack> createLine(String name, ArgumentType<?> argumentType, Command<CommandSourceStack> command) {
        return Commands.literal(name).then(Commands.argument(name, argumentType).executes(command));
    }

    private int insertBlockLine(CommandContext<CommandSourceStack> context) {
        var block = context.getArgument("block", BlockState.class).getBlockData();
        return insertLine(context, (hologram, line) -> hologram.addBlockLine(line).setBlock(block));
    }

    private int insertEntityLine(CommandContext<CommandSourceStack> context) {
        var entity = context.getArgument("entity", EntityType.class);
        return insertLine(context, (hologram, line) -> hologram.addEntityLine(entity, line));
    }

    private int insertItemLine(CommandContext<CommandSourceStack> context) {
        var item = context.getArgument("item", ItemStack.class);
        return insertLine(context, (hologram, line) -> hologram.addItemLine(line).setItemStack(item));
    }

    private int insertTextLine(CommandContext<CommandSourceStack> context) {
        var text = MiniMessage.miniMessage().deserialize(context.getArgument("text", String.class));
        return insertLine(context, (hologram, line) -> hologram.addTextLine(line).setText(text));
    }

    private int insertLine(CommandContext<CommandSourceStack> context, BiConsumer<Hologram, Integer> consumer) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var line = context.getArgument("line", int.class) - 1;
        consumer.accept(hologram, line);
        // todo: send message
        return SINGLE_SUCCESS;
    }
}
