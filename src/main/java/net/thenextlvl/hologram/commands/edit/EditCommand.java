package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class EditCommand extends SimpleCommand {
    protected final LineTargetResolver.Builder resolver;

    protected EditCommand(final HologramPlugin plugin, final String name, final LineTargetResolver.Builder resolver) {
        this(plugin, name, "holograms.command.line.edit." + name, resolver);
    }

    protected EditCommand(final HologramPlugin plugin, final String name, @Nullable final String permission, final LineTargetResolver.Builder resolver) {
        super(plugin, name, permission);
        this.resolver = resolver;
    }

    public final int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return this.run(context, this.resolver.build(context, this.plugin));
    }

    protected abstract int run(CommandContext<CommandSourceStack> var1, LineTargetResolver var2) throws CommandSyntaxException;

    protected final <V> String set(final @Nullable V currentValue, final @Nullable V newValue, final Consumer<V> setter, final String successKey) {
        if (Objects.equals(currentValue, newValue)) {
            return "nothing.changed";
        } else {
            setter.accept(newValue);
            return successKey;
        }
    }
}
