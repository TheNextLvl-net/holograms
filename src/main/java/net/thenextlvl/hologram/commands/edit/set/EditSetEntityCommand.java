package net.thenextlvl.hologram.commands.edit.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryKey;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.edit.EditCommand;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditSetEntityCommand extends EditCommand {
    private EditSetEntityCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "entity", null, resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final EditSetEntityCommand command = new EditSetEntityCommand(plugin, resolver);
        final RequiredArgumentBuilder<CommandSourceStack, EntityType> entity = Commands.argument("entity", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE));
        return command.create().then(entity.executes(command));
    }

    protected int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) {
        final EntityType entity = context.getArgument("entity", EntityType.class);
        return EditSetCommand.setLine(resolver, LineType.ENTITY, (line) -> line.setEntityType(entity), (hologram, line) -> hologram.setEntityLine(line, entity), (line, page) -> line.setEntityPage(page, entity));
    }
}
