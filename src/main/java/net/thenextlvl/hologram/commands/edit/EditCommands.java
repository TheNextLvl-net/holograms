package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.arguments.ColorArgumentType;
import net.thenextlvl.hologram.commands.arguments.EnumArgumentType;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.DisplayHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.StaticHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.bukkit.Color;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

/**
 * Factory for creating shared edit commands that work with both lines and pages.
 */
@NullMarked
public final class EditCommands {
    private EditCommands() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> alignment(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("alignment")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.alignment"))
                .then(Commands.argument("alignment", new EnumArgumentType<>(TextAlignment.class))
                        .executes(context -> editTyped(context, plugin, resolver, TextHologramLine.class, "hologram.type.text", line -> {
                            var alignment = context.getArgument("alignment", TextAlignment.class);
                            line.setAlignment(alignment);
                        }, "hologram.text-alignment")));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> append(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("append")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.append"))
                .then(Commands.argument("text", StringArgumentType.greedyString())
                        .executes(context -> editTyped(context, plugin, resolver, TextHologramLine.class, "hologram.type.text", line -> {
                            var text = context.getArgument("text", String.class);
                            line.getUnparsedText().map(s -> s.concat(text)).ifPresent(line::setUnparsedText);
                        }, "hologram.text.set")));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> prepend(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("prepend")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.prepend"))
                .then(Commands.argument("text", StringArgumentType.greedyString())
                        .executes(context -> editTyped(context, plugin, resolver, TextHologramLine.class, "hologram.type.text", line -> {
                            var text = context.getArgument("text", String.class);
                            line.getUnparsedText().map(s -> text.concat(s)).ifPresent(line::setUnparsedText);
                        }, "hologram.text.set")));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> replace(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("replace")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.replace"))
                .then(Commands.argument("match", StringArgumentType.string())
                        .then(Commands.argument("replacement", StringArgumentType.greedyString())
                                .executes(context -> editTyped(context, plugin, resolver, TextHologramLine.class, "hologram.type.text", line -> {
                                    var match = context.getArgument("match", String.class);
                                    var replacement = context.getArgument("replacement", String.class);
                                    line.getUnparsedText().map(s -> s.replace(match, replacement)).ifPresent(line::setUnparsedText);
                                }, "hologram.text.set"))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> backgroundColor(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("background-color")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.background-color"))
                .then(Commands.literal("reset")
                        .executes(context -> editTyped(context, plugin, resolver, TextHologramLine.class, "hologram.type.text",
                                line -> line.setBackgroundColor(null), "hologram.background-color.reset")))
                .then(Commands.argument("color", new ColorArgumentType())
                        .executes(context -> editTyped(context, plugin, resolver, TextHologramLine.class, "hologram.type.text", line -> {
                            var color = context.getArgument("color", Color.class);
                            line.setBackgroundColor(color);
                        }, "hologram.background-color")));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> billboard(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("billboard")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.billboard"))
                .then(Commands.argument("billboard", new EnumArgumentType<>(Billboard.class))
                        .executes(context -> editTyped(context, plugin, resolver, DisplayHologramLine.class, "hologram.type.display", line -> {
                            var billboard = context.getArgument("billboard", Billboard.class);
                            line.setBillboard(billboard);
                        }, "hologram.billboard")));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> brightness(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("brightness")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.brightness"))
                .then(Commands.literal("reset")
                        .executes(context -> editTyped(context, plugin, resolver, DisplayHologramLine.class, "hologram.type.display",
                                line -> line.setBrightness(null), "hologram.brightness.reset")))
                .then(Commands.argument("brightness", IntegerArgumentType.integer(0, 15))
                        .executes(context -> editTyped(context, plugin, resolver, DisplayHologramLine.class, "hologram.type.display", line -> {
                            var brightness = context.getArgument("brightness", int.class);
                            line.setBrightness(new Display.Brightness(brightness, brightness));
                        }, "hologram.brightness")))
                .then(Commands.argument("block", IntegerArgumentType.integer(0, 15))
                        .then(Commands.argument("sky", IntegerArgumentType.integer(0, 15))
                                .executes(context -> editTyped(context, plugin, resolver, DisplayHologramLine.class, "hologram.type.display", line -> {
                                    var block = context.getArgument("block", int.class);
                                    var sky = context.getArgument("sky", int.class);
                                    line.setBrightness(new Display.Brightness(block, sky));
                                }, "hologram.brightness"))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> defaultBackground(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("default-background")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.default-background"))
                .then(Commands.argument("default-background", BoolArgumentType.bool())
                        .executes(context -> editTyped(context, plugin, resolver, TextHologramLine.class, "hologram.type.text", line -> {
                            var defaultBackground = context.getArgument("default-background", boolean.class);
                            line.setDefaultBackground(defaultBackground);
                        }, "hologram.default-background")));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> glowColor(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("glow-color")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.glow-color"))
                .then(Commands.literal("reset")
                        .executes(context -> editTyped(context, plugin, resolver, StaticHologramLine.class, "hologram.type.single",
                                line -> line.setGlowColor(null), "hologram.line.glow-color.reset")))
                .then(Commands.argument("color", ArgumentTypes.namedColor())
                        .executes(context -> editTyped(context, plugin, resolver, StaticHologramLine.class, "hologram.type.single", line -> {
                            var color = context.getArgument("color", NamedTextColor.class);
                            line.setGlowColor(color);
                        }, "hologram.line.glow-color")))
                .then(Commands.argument("hex", ArgumentTypes.hexColor())
                        .executes(context -> editTyped(context, plugin, resolver, StaticHologramLine.class, "hologram.type.single", line -> {
                            var color = context.getArgument("hex", TextColor.class);
                            line.setGlowColor(color);
                        }, "hologram.line.glow-color")));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> glowing(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("glowing")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.glowing"))
                .then(Commands.argument("glowing", BoolArgumentType.bool())
                        .executes(context -> {
                            var glowing = context.getArgument("glowing", boolean.class);
                            return editTyped(context, plugin, resolver, StaticHologramLine.class, "hologram.type.single",
                                    line -> line.setGlowing(glowing),
                                    glowing ? "hologram.line.glow.enabled" : "hologram.line.glow.disabled");
                        }));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> interpolationDelay(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("interpolation-delay")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.interpolation-delay"))
                .then(Commands.argument("delay", IntegerArgumentType.integer(0))
                        .executes(context -> editTyped(context, plugin, resolver, DisplayHologramLine.class, "hologram.type.display", line -> {
                            var delay = context.getArgument("delay", int.class);
                            line.setInterpolationDelay(delay);
                        }, "hologram.interpolation-delay")));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> interpolationDuration(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("interpolation-duration")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.interpolation-duration"))
                .then(Commands.argument("duration", IntegerArgumentType.integer(0))
                        .executes(context -> editTyped(context, plugin, resolver, DisplayHologramLine.class, "hologram.type.display", line -> {
                            var duration = context.getArgument("duration", int.class);
                            line.setInterpolationDuration(duration);
                        }, "hologram.interpolation-duration")));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> offset(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("offset")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.offset"))
                .then(Commands.literal("reset")
                        .executes(context -> editForOffset(context, plugin, resolver, new Vector3f())))
                .then(Commands.argument("x", FloatArgumentType.floatArg())
                        .then(Commands.argument("y", FloatArgumentType.floatArg())
                                .then(Commands.argument("z", FloatArgumentType.floatArg())
                                        .executes(context -> {
                                            var x = context.getArgument("x", float.class);
                                            var y = context.getArgument("y", float.class);
                                            var z = context.getArgument("z", float.class);
                                            return editForOffset(context, plugin, resolver, new Vector3f(x, y, z));
                                        }))));
    }

    private static int editForOffset(CommandContext<CommandSourceStack> context, HologramPlugin plugin, LineTargetResolver resolver, Vector3f offset) {
        var target = resolver.resolve(context, plugin);
        if (target.isEmpty()) return 0;

        var result = target.get();
        var line = result.line();

        if (line instanceof DisplayHologramLine displayLine) {
            var transformation = displayLine.getTransformation();
            transformation.getTranslation().set(offset);
            displayLine.setTransformation(transformation);
        } else if (line instanceof EntityHologramLine entityLine) {
            entityLine.setOffset(offset);
        } else {
            plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.type.display",
                    buildPlaceholders(result));
            return 0;
        }

        plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.offset", buildPlaceholders(result));
        return SINGLE_SUCCESS;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> opacity(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("opacity")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.opacity"))
                .then(Commands.argument("opacity", FloatArgumentType.floatArg(0, 100))
                        .executes(context -> editTyped(context, plugin, resolver, TextHologramLine.class, "hologram.type.text", line -> {
                            var opacity = context.getArgument("opacity", float.class);
                            line.setTextOpacity(opacity);
                        }, "hologram.opacity")));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> scale(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("scale")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.scale"))
                .then(Commands.argument("scale", FloatArgumentType.floatArg(0.1f))
                        .executes(context -> editForScale(context, plugin, resolver)))
                .then(Commands.argument("x", FloatArgumentType.floatArg(0.1f))
                        .then(Commands.argument("y", FloatArgumentType.floatArg(0.1f))
                                .then(Commands.argument("z", FloatArgumentType.floatArg(0.1f))
                                        .executes(context -> editForScale(context, plugin, resolver)))));
    }

    private static int editForScale(CommandContext<CommandSourceStack> context, HologramPlugin plugin, LineTargetResolver resolver) {
        var target = resolver.resolve(context, plugin);
        if (target.isEmpty()) return 0;

        var result = target.get();
        var line = result.line();

        Vector3f scale = tryGetArgument(context, "scale", float.class)
                .map(Vector3f::new)
                .orElseGet(() -> new Vector3f(
                        context.getArgument("x", float.class),
                        context.getArgument("y", float.class),
                        context.getArgument("z", float.class)
                ));

        if (line instanceof DisplayHologramLine displayLine) {
            var transformation = displayLine.getTransformation();
            transformation.getScale().set(scale);
            displayLine.setTransformation(transformation);
        } else if (line instanceof EntityHologramLine entityLine) {
            entityLine.setScale(scale.y());
        } else {
            plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.type.display",
                    buildPlaceholders(result));
            return 0;
        }

        plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.scale", buildPlaceholders(result));
        return SINGLE_SUCCESS;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> seeThrough(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("see-through")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.see-through"))
                .then(Commands.argument("see-through", BoolArgumentType.bool())
                        .executes(context -> editTyped(context, plugin, resolver, TextHologramLine.class, "hologram.type.text", line -> {
                            var seeThrough = context.getArgument("see-through", boolean.class);
                            line.setSeeThrough(seeThrough);
                        }, "hologram.see-through")));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> shadowed(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("shadowed")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.shadowed"))
                .then(Commands.argument("shadowed", BoolArgumentType.bool())
                        .executes(context -> editTyped(context, plugin, resolver, TextHologramLine.class, "hologram.type.text", line -> {
                            var shadowed = context.getArgument("shadowed", boolean.class);
                            line.setShadowed(shadowed);
                        }, "hologram.shadowed")));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> teleportDuration(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("teleport-duration")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.teleport-duration"))
                .then(Commands.argument("duration", IntegerArgumentType.integer(0, 59))
                        .executes(context -> editTyped(context, plugin, resolver, DisplayHologramLine.class, "hologram.type.display", line -> {
                            var duration = context.getArgument("duration", int.class);
                            line.setTeleportDuration(duration);
                        }, "hologram.teleport-duration")));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> transformation(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("transformation")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.transformation"))
                .then(Commands.argument("transformation", new EnumArgumentType<>(ItemDisplay.ItemDisplayTransform.class))
                        .executes(context -> editTyped(context, plugin, resolver, ItemHologramLine.class, "hologram.type.item", line -> {
                            var transformation = context.getArgument("transformation", ItemDisplay.ItemDisplayTransform.class);
                            line.setItemDisplayTransform(transformation);
                        }, "hologram.transformation")));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> set(HologramPlugin plugin, LineTargetResolver resolver) {
        return Commands.literal("set")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.set"))
                .then(setType("block", ArgumentTypes.blockState(), plugin, resolver, (context, line) -> {
                    var block = context.getArgument("block", BlockState.class).getBlockData();
                    if (line instanceof BlockHologramLine blockLine) {
                        blockLine.setBlock(block);
                        return true;
                    }
                    return false;
                }, "hologram.type.block"))
                .then(setType("entity", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE), plugin, resolver, (context, line) -> {
                    var entity = context.getArgument("entity", EntityType.class);
                    if (line instanceof EntityHologramLine entityLine) {
                        // Entity lines need to be replaced, not just modified
                        return false; // Can't change entity type in-place
                    }
                    return false;
                }, "hologram.type.entity"))
                .then(setType("item", ArgumentTypes.itemStack(), plugin, resolver, (context, line) -> {
                    var item = context.getArgument("item", ItemStack.class);
                    if (line instanceof ItemHologramLine itemLine) {
                        itemLine.setItemStack(item);
                        return true;
                    }
                    return false;
                }, "hologram.type.item"))
                .then(setType("text", StringArgumentType.greedyString(), plugin, resolver, (context, line) -> {
                    var text = context.getArgument("text", String.class);
                    if (line instanceof TextHologramLine textLine) {
                        textLine.setUnparsedText(text);
                        return true;
                    }
                    return false;
                }, "hologram.type.text"));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> setType(
            String name,
            ArgumentType<?> argumentType,
            HologramPlugin plugin,
            LineTargetResolver resolver,
            SetHandler handler,
            String wrongTypeKey
    ) {
        return Commands.literal(name)
                .then(Commands.argument(name, argumentType)
                        .executes(context -> {
                            var target = resolver.resolve(context, plugin);
                            if (target.isEmpty()) return 0;

                            var result = target.get();
                            if (handler.apply(context, result.line())) {
                                plugin.bundle().sendMessage(context.getSource().getSender(),
                                        result.isPage() ? "hologram.page.set" : "hologram.line.set",
                                        buildPlaceholders(result));
                                return SINGLE_SUCCESS;
                            } else {
                                plugin.bundle().sendMessage(context.getSource().getSender(), wrongTypeKey,
                                        buildPlaceholders(result));
                                return 0;
                            }
                        }));
    }

    @FunctionalInterface
    private interface SetHandler {
        boolean apply(CommandContext<CommandSourceStack> context, HologramLine line);
    }

    private static <T extends HologramLine> int editTyped(
            CommandContext<CommandSourceStack> context,
            HologramPlugin plugin,
            LineTargetResolver resolver,
            Class<T> type,
            String wrongTypeKey,
            Consumer<T> change,
            String successKey
    ) {
        var target = resolver.resolve(context, plugin);
        if (target.isEmpty()) return 0;

        var result = target.get();
        if (!type.isInstance(result.line())) {
            plugin.bundle().sendMessage(context.getSource().getSender(), wrongTypeKey, buildPlaceholders(result));
            return 0;
        }

        change.accept(type.cast(result.line()));
        plugin.bundle().sendMessage(context.getSource().getSender(), successKey, buildPlaceholders(result));
        return SINGLE_SUCCESS;
    }

    private static TagResolver[] buildPlaceholders(LineEditTarget target) {
        var list = new ArrayList<TagResolver>();
        list.add(Placeholder.unparsed("hologram", target.hologram().getName()));
        list.add(Formatter.number("line", target.displayLineIndex()));
        if (target.isPage()) {
            list.add(Formatter.number("page", target.displayPageIndex()));
        }
        return list.toArray(TagResolver[]::new);
    }

    private static <T> Optional<T> tryGetArgument(CommandContext<CommandSourceStack> context, String name, Class<T> type) {
        try {
            return Optional.of(context.getArgument(name, type));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("No such argument '" + name + "' exists on this command"))
                return Optional.empty();
            throw e;
        }
    }

    /**
     * Adds all edit commands to an argument builder.
     */
    public static <T extends ArgumentBuilder<CommandSourceStack, T>> T addAllEditCommands(
            T builder,
            HologramPlugin plugin,
            LineTargetResolver resolver
    ) {
        return builder
                .then(alignment(plugin, resolver))
                .then(append(plugin, resolver))
                .then(backgroundColor(plugin, resolver))
                .then(billboard(plugin, resolver))
                .then(brightness(plugin, resolver))
                .then(defaultBackground(plugin, resolver))
                .then(glowColor(plugin, resolver))
                .then(glowing(plugin, resolver))
                .then(interpolationDelay(plugin, resolver))
                .then(interpolationDuration(plugin, resolver))
                .then(offset(plugin, resolver))
                .then(opacity(plugin, resolver))
                .then(prepend(plugin, resolver))
                .then(replace(plugin, resolver))
                .then(scale(plugin, resolver))
                .then(seeThrough(plugin, resolver))
                .then(set(plugin, resolver))
                .then(shadowed(plugin, resolver))
                .then(teleportDuration(plugin, resolver))
                .then(transformation(plugin, resolver));
    }
}
