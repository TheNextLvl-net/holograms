package net.thenextlvl.hologram.commands.action.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ActionTypes;
import net.thenextlvl.hologram.action.UnparsedTitle;
import net.thenextlvl.hologram.commands.action.ActionTargetResolver;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class SendTitleCommand extends HologramActionCommand<UnparsedTitle> {
    private SendTitleCommand(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        super(plugin, ActionTypes.types().sendTitle(), "send-title", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        final var command = new SendTitleCommand(plugin, resolver);
        final var title = Commands.argument("title", StringArgumentType.string()).executes(command);
        final var subtitle = Commands.argument("subtitle", StringArgumentType.string()).executes(command);
        final var times = command.titleTimesArgument();
        return command.create().then(title.then(subtitle.then(times)));
    }

    private ArgumentBuilder<CommandSourceStack, ?> titleTimesArgument() {
        final var fadeIn = Commands.argument("fade-in", ArgumentTypes.time());
        final var stay = Commands.argument("stay", ArgumentTypes.time());
        final var fadeOut = Commands.argument("fade-out", ArgumentTypes.time());
        return fadeIn.then(stay.then(fadeOut.executes(this)));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return resolverBuilder.build(context, plugin).resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var title = context.getArgument("title", String.class);
            final var subtitle = tryGetArgument(context, "subtitle", String.class).orElse("");
            final var fadeIn = tryGetArgument(context, "fade-in", int.class).map(Ticks::duration).orElse(null);
            final var stay = tryGetArgument(context, "stay", int.class).map(Ticks::duration).orElse(null);
            final var fadeOut = tryGetArgument(context, "fade-out", int.class).map(Ticks::duration).orElse(null);
            final var times = fadeIn != null && stay != null && fadeOut != null ? Title.Times.times(fadeIn, stay, fadeOut) : null;
            return addAction(context, hologram, line, new UnparsedTitle(title, subtitle, times));
        });
    }
}
