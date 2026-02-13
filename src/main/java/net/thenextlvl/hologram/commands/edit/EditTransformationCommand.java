package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.arguments.EnumArgumentType;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditTransformationCommand extends EditCommand {
    private EditTransformationCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "transformation", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditTransformationCommand(plugin, resolver);
        final var named = Commands.argument("transformation", new EnumArgumentType<>(ItemDisplayTransform.class));
        return command.create().then(named.executes(command)).executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var transformation = tryGetArgument(context, "transformation", ItemDisplayTransform.class);

            final var transformationName = plugin.bundle().component(switch (transformation.orElseGet(line::getItemDisplayTransform)) {
                case FIRSTPERSON_LEFTHAND -> "transformation.firstperson-lefthand";
                case FIRSTPERSON_RIGHTHAND -> "transformation.firstperson-righthand";
                case FIXED -> "transformation.fixed";
                case GROUND -> "transformation.ground";
                case GUI -> "transformation.gui";
                case HEAD -> "transformation.head";
                case NONE -> "transformation.none";
                case THIRDPERSON_LEFTHAND -> "transformation.thirdperson-lefthand";
                case THIRDPERSON_RIGHTHAND -> "transformation.thirdperson-righthand";
            }, context.getSource().getSender());

            final var message = transformation.map(value -> {
                return set(line.getItemDisplayTransform(), value, line::setItemDisplayTransform, "hologram.transformation");
            }).orElse("hologram.transformation.query");

            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    TagResolver.resolver(placeholders),
                    Placeholder.component("transformation", transformationName));
            return SINGLE_SUCCESS;
        }, LineType.ITEM);
    }
}
