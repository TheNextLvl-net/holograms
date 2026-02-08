package net.thenextlvl.hologram.commands.action;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.commands.suggestions.HologramActionSuggestionProvider;
import net.thenextlvl.hologram.line.HologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Stream;

@NullMarked
abstract class ActionCommand extends SimpleCommand {
    protected final ActionTargetResolver.Builder resolverBuilder;

    protected ActionCommand(final HologramPlugin plugin, final String name, @Nullable final String permission, final ActionTargetResolver.Builder resolverBuilder) {
        super(plugin, name, permission);
        this.resolverBuilder = resolverBuilder;
    }

    @Override
    public final int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return resolverBuilder.build(context, plugin).resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var sender = context.getSource().getSender();
            final var actionName = context.getArgument("action", String.class);
            final var action = line.getAction(actionName).orElse(null);
            if (action == null) {
                plugin.bundle().sendMessage(sender, "hologram.action.not_found",
                        Placeholder.parsed("hologram", hologram.getName()),
                        Placeholder.unparsed("action", actionName));
                return 0;
            }
            return run(context, hologram, line, action, actionName, placeholders);
        });
    }

    public abstract int run(CommandContext<CommandSourceStack> context, Hologram hologram, HologramLine line, ClickAction<?> action, String actionName, TagResolver... placeholders) throws CommandSyntaxException;

    protected final TagResolver[] concat(final TagResolver[] placeholders, final TagResolver... resolvers) {
        return Stream.concat(Arrays.stream(placeholders), Arrays.stream(resolvers)).toArray(TagResolver[]::new);
    }

    protected static ArgumentBuilder<CommandSourceStack, ?> actionArgument(final HologramPlugin plugin) {
        return Commands.argument("action", StringArgumentType.word())
                .suggests(new HologramActionSuggestionProvider());
    }
}
