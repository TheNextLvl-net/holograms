package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditPlayerHeadCommand extends EditCommand {
    private EditPlayerHeadCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "player-head", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditPlayerHeadCommand(plugin, resolver);
        final var named = Commands.argument("player-head", BoolArgumentType.bool());
        return command.create().then(named.executes(command)).executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var playerHead = tryGetArgument(context, "player-head", boolean.class);

            final var message = playerHead.map(value -> {
                final var successKey = value ? "hologram.line.player-head.enabled" : "hologram.line.player-head.disabled";
                return set(value, line::setPlayerHead, successKey);
            }).orElseGet(() -> line.isPlayerHead()
                    ? "hologram.player-head.query.enabled"
                    : "hologram.player-head.query.disabled");

            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    TagResolver.resolver(placeholders));
            return SINGLE_SUCCESS;
        }, LineType.ITEM);
    }
}
