package net.thenextlvl.hologram.commands.edit.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@NullMarked
public final class EditSetCommand extends BrigadierCommand {
    private EditSetCommand(final HologramPlugin plugin) {
        super(plugin, "set", "holograms.command.line.edit.set");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditSetCommand(plugin).create()
                .then(EditSetBlockCommand.create(plugin, resolver))
                .then(EditSetEntityCommand.create(plugin, resolver))
                .then(EditSetItemCommand.create(plugin, resolver))
                .then(EditSetTextCommand.create(plugin, resolver));
        if (resolver.equals(LineTargetResolver.LINE)) {
            command.then(EditSetPagedCommand.create(plugin, resolver));
        }
        return command;
    }

    static <T extends HologramLine> int setLine(
            final LineTargetResolver resolver,
            final LineTargetResolver.LineType<T> lineType,
            final Consumer<T> setter,
            final BiFunction<Hologram, Integer, T> lineMutator,
            final BiFunction<PagedHologramLine, Integer, T> pageMutator
    ) {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            if (!lineType.clazz().isInstance(line)) {
                if (pageIndex != null) {
                    final var pageLine = hologram.getLine(lineIndex, PagedHologramLine.class).orElseThrow();
                    final var page = pageLine.getPage(pageIndex).orElseThrow();
                    if (lineType.clazz().isInstance(page)) {
                        line = lineType.clazz().cast(page);
                    } else {
                        line = pageMutator.apply(pageLine, pageIndex);
                    }
                } else {
                    line = lineMutator.apply(hologram, lineIndex);
                }
            }

            setter.accept(lineType.clazz().cast(line));
            final var sender = resolver.context().getSource().getSender();
            resolver.plugin().bundle().sendMessage(sender, "hologram.line.set", placeholders);
            return SINGLE_SUCCESS;
        }, LineType.ANY);
    }
}
