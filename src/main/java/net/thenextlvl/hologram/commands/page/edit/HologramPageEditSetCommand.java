package net.thenextlvl.hologram.commands.page.edit;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.commands.suggestions.PageSuggestionProvider;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@NullMarked
public final class HologramPageEditSetCommand extends BrigadierCommand {
    private HologramPageEditSetCommand(final HologramPlugin plugin) {
        super(plugin, "set", "holograms.command.page.edit.set");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageEditSetCommand(plugin);
        final var page = Commands.argument("page", IntegerArgumentType.integer(1))
                .suggests(PageSuggestionProvider.INSTANCE);
        return command.create().then(page
                .then(command.setPage("block", ArgumentTypes.blockState(), command::setBlockPage))
                .then(command.setPage("item", ArgumentTypes.itemStack(), command::setItemPage))
                .then(command.setPage("text", StringArgumentType.greedyString(), command::setTextPage)));
    }

    private LiteralArgumentBuilder<CommandSourceStack> setPage(final String name, final ArgumentType<?> argumentType, final Command<CommandSourceStack> command) {
        return Commands.literal(name).then(Commands.argument(name, argumentType).executes(command));
    }

    private int setBlockPage(final CommandContext<CommandSourceStack> context) {
        final var block = context.getArgument("block", BlockState.class).getBlockData();
        return setPage(context, BlockHologramLine.class, page -> page.setBlock(block));
    }

    private int setItemPage(final CommandContext<CommandSourceStack> context) {
        final var item = context.getArgument("item", ItemStack.class);
        return setPage(context, ItemHologramLine.class, page -> page.setItemStack(item));
    }

    private int setTextPage(final CommandContext<CommandSourceStack> context) {
        final var text = context.getArgument("text", String.class);
        return setPage(context, TextHologramLine.class, page -> page.setUnparsedText(text));
    }

    private <T extends HologramLine> int setPage(final CommandContext<CommandSourceStack> context, final Class<T> type, final Consumer<T> consumer) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineIndex = context.getArgument("line", int.class) - 1;
        final var pageIndex = context.getArgument("page", int.class) - 1;
        final var line = hologram.getLine(lineIndex, PagedHologramLine.class);

        if (line.isEmpty()) {
            plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.type.paged");
            return 0;
        }

        final var pagedLine = line.get();
        final var pages = pagedLine.getPages();

        if (pageIndex < 0 || pageIndex >= pages.size()) {
            plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.page.invalid",
                    Placeholder.unparsed("hologram", hologram.getName()),
                    Formatter.number("line", lineIndex + 1),
                    Formatter.number("page", pageIndex + 1));
            return 0;
        }

        final var page = pages.get(pageIndex);
        if (!type.isInstance(page)) {
            plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.page.type.mismatch",
                    Placeholder.unparsed("hologram", hologram.getName()),
                    Formatter.number("line", lineIndex + 1),
                    Formatter.number("page", pageIndex + 1));
            return 0;
        }

        consumer.accept(type.cast(page));
        plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.page.set",
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineIndex + 1),
                Formatter.number("page", pageIndex + 1));
        return SINGLE_SUCCESS;
    }
}
