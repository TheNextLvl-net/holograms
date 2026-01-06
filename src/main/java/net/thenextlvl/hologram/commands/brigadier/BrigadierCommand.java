package net.thenextlvl.hologram.commands.brigadier;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.resolvers.ArgumentResolver;
import net.thenextlvl.hologram.HologramPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public abstract class BrigadierCommand {
    protected final HologramPlugin plugin;

    private final @Nullable String permission;
    private final String name;

    protected BrigadierCommand(HologramPlugin plugin, String name, @Nullable String permission) {
        this.plugin = plugin;
        this.permission = permission;
        this.name = name;
    }

    protected LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal(name).requires(this::canUse);
    }

    protected boolean canUse(CommandSourceStack source) {
        return permission == null || source.getSender().hasPermission(permission);
    }

    protected <T> Optional<T> tryGetArgument(CommandContext<CommandSourceStack> context, String name, Class<T> type) {
        try {
            return Optional.of(context.getArgument(name, type));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("No such argument '" + name + "' exists on this command"))
                return Optional.empty();
            throw e;
        }
    }

    public <T> Optional<T> resolveArgument(CommandContext<CommandSourceStack> context, String name, Class<? extends ArgumentResolver<T>> type) throws CommandSyntaxException {
        var resolver = tryGetArgument(context, name, type).orElse(null);
        return resolver != null ? Optional.of(resolver.resolve(context.getSource())) : Optional.empty();
    }
}
