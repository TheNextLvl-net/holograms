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
import net.thenextlvl.hologram.line.PagedHologramLine;
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
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@NullMarked
public final class EditCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> alignment(final HologramPlugin plugin, final LineTargetResolver resolver) {
        return create("alignment", plugin, resolver)
                .textType()
                .enumArg(TextAlignment.class, TextHologramLine::setAlignment)
                .successMessage("hologram.text-alignment")
                .build();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> append(final HologramPlugin plugin, final LineTargetResolver resolver) {
        return create("append", plugin, resolver)
                .textType()
                .textArg((line, text) -> line.getUnparsedText().map(s -> s.concat(text)).ifPresent(line::setUnparsedText))
                .successMessage("hologram.text.set")
                .build();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> prepend(final HologramPlugin plugin, final LineTargetResolver resolver) {
        return create("prepend", plugin, resolver)
                .textType()
                .textArg((line, text) -> line.getUnparsedText().map(text::concat).ifPresent(line::setUnparsedText))
                .successMessage("hologram.text.set")
                .build();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> replace(final HologramPlugin plugin, final LineTargetResolver resolver) {
        return Commands.literal("replace")
                .requires(requiresPermission("replace"))
                .then(Commands.argument("match", StringArgumentType.string())
                        .then(Commands.argument("replacement", StringArgumentType.greedyString())
                                .executes(context -> editTyped(context, plugin, resolver, TextHologramLine.class, "hologram.type.text", line -> {
                                    final var match = context.getArgument("match", String.class);
                                    final var replacement = context.getArgument("replacement", String.class);
                                    line.getUnparsedText().map(s -> s.replace(match, replacement)).ifPresent(line::setUnparsedText);
                                }, "hologram.text.set"))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> backgroundColor(final HologramPlugin plugin, final LineTargetResolver resolver) {
        return create("background-color", plugin, resolver)
                .textType()
                .reset(line -> line.setBackgroundColor(null), "hologram.background-color.reset")
                .arg("color", new ColorArgumentType(), Color.class, TextHologramLine::setBackgroundColor)
                .successMessage("hologram.background-color")
                .build();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> billboard(final HologramPlugin plugin, final LineTargetResolver resolver) {
        return create("billboard", plugin, resolver)
                .displayType()
                .enumArg(Billboard.class, DisplayHologramLine::setBillboard)
                .successMessage("hologram.billboard")
                .build();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> brightness(final HologramPlugin plugin, final LineTargetResolver resolver) {
        final var brightnessArg = Commands.argument("brightness", IntegerArgumentType.integer(0, 15));
        final var blockArg = Commands.argument("block", IntegerArgumentType.integer(0, 15));
        final var skyArg = Commands.argument("sky", IntegerArgumentType.integer(0, 15));
        return Commands.literal("brightness")
                .requires(requiresPermission("brightness"))
                .then(Commands.literal("reset").executes(context -> editTyped(context, plugin, resolver, DisplayHologramLine.class, "hologram.type.display",
                        line -> line.setBrightness(null), "hologram.brightness.reset")))
                .then(brightnessArg.executes(context -> editTyped(context, plugin, resolver, DisplayHologramLine.class, "hologram.type.display", line -> {
                    final var brightness = context.getArgument("brightness", int.class);
                    line.setBrightness(new Display.Brightness(brightness, brightness));
                }, "hologram.brightness")))
                .then(blockArg.then(skyArg.executes(context -> editTyped(context, plugin, resolver, DisplayHologramLine.class, "hologram.type.display", line -> {
                    final var block = context.getArgument("block", int.class);
                    final var sky = context.getArgument("sky", int.class);
                    line.setBrightness(new Display.Brightness(block, sky));
                }, "hologram.brightness"))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> defaultBackground(final HologramPlugin plugin, final LineTargetResolver resolver) {
        return create("default-background", plugin, resolver)
                .textType()
                .boolArg(TextHologramLine::setDefaultBackground)
                .successMessage("hologram.default-background")
                .build();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> glowColor(final HologramPlugin plugin, final LineTargetResolver resolver) {
        return create("glow-color", plugin, resolver)
                .typed(StaticHologramLine.class, "hologram.type.single")
                .reset(line -> line.setGlowColor(null), "hologram.line.glow-color.reset")
                .arg("color", ArgumentTypes.namedColor(), NamedTextColor.class, StaticHologramLine::setGlowColor)
                .arg("hex", ArgumentTypes.hexColor(), TextColor.class, StaticHologramLine::setGlowColor)
                .successMessage("hologram.line.glow-color")
                .build();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> glowing(final HologramPlugin plugin, final LineTargetResolver resolver) {
        final var glowingArg = Commands.argument("glowing", BoolArgumentType.bool());
        return Commands.literal("glowing")
                .requires(requiresPermission("glowing"))
                .then(glowingArg.executes(context -> {
                    final var glowing = context.getArgument("glowing", boolean.class);
                    return editTyped(context, plugin, resolver, StaticHologramLine.class, "hologram.type.single",
                            line -> line.setGlowing(glowing),
                            glowing ? "hologram.line.glow.enabled" : "hologram.line.glow.disabled");
                }));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> interpolationDelay(final HologramPlugin plugin, final LineTargetResolver resolver) {
        return create("interpolation-delay", plugin, resolver)
                .displayType()
                .intArg("delay", 0, DisplayHologramLine::setInterpolationDelay)
                .successMessage("hologram.interpolation-delay")
                .build();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> interpolationDuration(final HologramPlugin plugin, final LineTargetResolver resolver) {
        return create("interpolation-duration", plugin, resolver)
                .displayType()
                .intArg("duration", 0, DisplayHologramLine::setInterpolationDuration)
                .successMessage("hologram.interpolation-duration")
                .build();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> offset(final HologramPlugin plugin, final LineTargetResolver resolver) {
        return Commands.literal("offset")
                .requires(requiresPermission("offset"))
                .then(Commands.literal("reset")
                        .executes(context -> editForOffset(context, plugin, resolver, new Vector3f())))
                .then(vector3fArguments(-16, 16, (context, vector) -> editForOffset(context, plugin, resolver, vector)));
    }

    private static int editForOffset(final CommandContext<CommandSourceStack> context, final HologramPlugin plugin, final LineTargetResolver resolver, final Vector3f offset) {
        return editDisplayOrEntity(context, plugin, resolver, "hologram.offset",
                displayLine -> displayLine.getTransformation().getTranslation().set(offset),
                entityLine -> entityLine.setOffset(offset));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> opacity(final HologramPlugin plugin, final LineTargetResolver resolver) {
        return create("opacity", plugin, resolver)
                .textType()
                .arg("opacity", FloatArgumentType.floatArg(0, 100), float.class, TextHologramLine::setTextOpacity)
                .successMessage("hologram.opacity")
                .build();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> scale(final HologramPlugin plugin, final LineTargetResolver resolver) {
        return Commands.literal("scale")
                .requires(requiresPermission("scale"))
                .then(Commands.argument("scale", FloatArgumentType.floatArg(0.1f))
                        .executes(context -> editForScale(context, plugin, resolver)))
                .then(vector3fArguments(0.1f, 100, (context, vector) -> editForScale(context, plugin, resolver)));
    }

    private static int editForScale(final CommandContext<CommandSourceStack> context, final HologramPlugin plugin, final LineTargetResolver resolver) {
        final var scale = tryGetArgument(context, "scale", float.class)
                .map(Vector3f::new).orElseGet(() -> getVector3f(context));

        return editDisplayOrEntity(context, plugin, resolver, "hologram.scale",
                displayLine -> displayLine.getTransformation().getScale().set(scale),
                entityLine -> entityLine.setScale(scale.y()));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> seeThrough(final HologramPlugin plugin, final LineTargetResolver resolver) {
        return create("see-through", plugin, resolver)
                .textType()
                .boolArg(TextHologramLine::setSeeThrough)
                .successMessage("hologram.see-through")
                .build();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> shadowed(final HologramPlugin plugin, final LineTargetResolver resolver) {
        return create("shadowed", plugin, resolver)
                .textType()
                .boolArg(TextHologramLine::setShadowed)
                .successMessage("hologram.shadowed")
                .build();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> teleportDuration(final HologramPlugin plugin, final LineTargetResolver resolver) {
        return create("teleport-duration", plugin, resolver)
                .displayType()
                .intArg("duration", 0, DisplayHologramLine::setTeleportDuration)
                .successMessage("hologram.teleport-duration")
                .build();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> transformation(final HologramPlugin plugin, final LineTargetResolver resolver) {
        return create("transformation", plugin, resolver)
                .typed(ItemHologramLine.class, "hologram.type.item")
                .enumArg(ItemDisplay.ItemDisplayTransform.class, ItemHologramLine::setItemDisplayTransform)
                .successMessage("hologram.transformation")
                .build();
    }

    private static EditBuilder<HologramLine> create(final String name, final HologramPlugin plugin, final LineTargetResolver resolver) {
        return new EditBuilder<>(name, plugin, resolver, HologramLine.class, "hologram.type.line");
    }

    private static final class EditBuilder<T extends HologramLine> {
        private final String name;
        private final HologramPlugin plugin;
        private final LineTargetResolver resolver;
        private final Class<T> lineType;
        private final String wrongTypeKey;
        private final LiteralArgumentBuilder<CommandSourceStack> builder;
        private @Nullable String successKey = null;

        private EditBuilder(final String name, final HologramPlugin plugin, final LineTargetResolver resolver, final Class<T> lineType, final String wrongTypeKey) {
            this.name = name;
            this.plugin = plugin;
            this.resolver = resolver;
            this.lineType = lineType;
            this.wrongTypeKey = wrongTypeKey;
            this.builder = Commands.literal(name).requires(requiresPermission(name));
        }

        private <U extends HologramLine> EditBuilder<U> typed(final Class<U> type, final String wrongTypeKey) {
            return new EditBuilder<>(name, plugin, resolver, type, wrongTypeKey);
        }

        EditBuilder<TextHologramLine> textType() {
            return typed(TextHologramLine.class, "hologram.type.text");
        }

        EditBuilder<DisplayHologramLine> displayType() {
            return typed(DisplayHologramLine.class, "hologram.type.display");
        }

        EditBuilder<T> successMessage(final String key) {
            this.successKey = key;
            return this;
        }

        <E extends Enum<E>> EditBuilder<T> enumArg(final Class<E> enumClass, final BiConsumer<T, E> setter) {
            return arg(name, new EnumArgumentType<>(enumClass), enumClass, setter);
        }

        EditBuilder<T> boolArg(final BiConsumer<T, Boolean> setter) {
            return arg(name, BoolArgumentType.bool(), boolean.class, setter);
        }

        EditBuilder<T> intArg(final String argName, final int min, final BiConsumer<T, Integer> setter) {
            return arg(argName, IntegerArgumentType.integer(min), int.class, setter);
        }

        EditBuilder<T> textArg(final BiConsumer<T, String> action) {
            return arg("text", StringArgumentType.greedyString(), String.class, action);
        }

        <A> EditBuilder<T> arg(final String argName, final ArgumentType<A> argType, final Class<A> valueType, final BiConsumer<T, A> setter) {
            builder.then(Commands.argument(argName, argType).executes(context -> {
                return editTyped(context, plugin, resolver, lineType, wrongTypeKey, line -> {
                    setter.accept(line, context.getArgument(argName, valueType));
                }, Objects.requireNonNull(successKey, "successKey cannot be null"));
            }));
            return this;
        }

        EditBuilder<T> reset(final Consumer<T> resetAction, final String resetSuccessKey) {
            builder.then(Commands.literal("reset").executes(context -> {
                return editTyped(context, plugin, resolver, lineType, wrongTypeKey, resetAction, resetSuccessKey);
            }));
            return this;
        }

        LiteralArgumentBuilder<CommandSourceStack> build() {
            return builder;
        }
    }

    private static Predicate<CommandSourceStack> requiresPermission(final String command) {
        return source -> source.getSender().hasPermission("holograms.command.edit." + command);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> vector3fArguments(final float min, final float max, final BiFunction<CommandContext<CommandSourceStack>, Vector3f, Integer> handler) {
        final var x = Commands.argument("x", FloatArgumentType.floatArg(min, max));
        final var y = Commands.argument("y", FloatArgumentType.floatArg(min, max));
        final var z = Commands.argument("z", FloatArgumentType.floatArg(min, max));
        return x.then(y.then(z.executes(context -> handler.apply(context, getVector3f(context)))));
    }

    private static Vector3f getVector3f(final CommandContext<CommandSourceStack> context) {
        final var x = context.getArgument("x", float.class);
        final var y = context.getArgument("y", float.class);
        final var z = context.getArgument("z", float.class);
        return new Vector3f(x, y, z);
    }

    private static int editDisplayOrEntity(
            final CommandContext<CommandSourceStack> context, final HologramPlugin plugin, final LineTargetResolver resolver,
            final String successKey, final Consumer<DisplayHologramLine> displayAction, final Consumer<EntityHologramLine> entityAction) {
        final var target = resolver.resolve(context, plugin);
        if (target.isEmpty()) return 0;

        final var result = target.get();
        final var line = result.line();

        if (line instanceof final DisplayHologramLine displayLine) {
            displayAction.accept(displayLine);
            displayLine.setTransformation(displayLine.getTransformation());
        } else if (line instanceof final EntityHologramLine entityLine) {
            entityAction.accept(entityLine);
        } else {
            plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.type.display", buildPlaceholders(result));
            return 0;
        }

        plugin.bundle().sendMessage(context.getSource().getSender(), successKey, buildPlaceholders(result));
        return SINGLE_SUCCESS;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> set(final HologramPlugin plugin, final LineTargetResolver resolver) {
        return Commands.literal("set")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.set"))
                .then(setType("block", ArgumentTypes.blockState(), plugin, BlockHologramLine.class, resolver, (context, line) -> {
                    final var block = context.getArgument("block", BlockState.class).getBlockData();
                    line.setBlock(block);
                    return true;
                }))
                .then(setType("entity", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE), plugin, EntityHologramLine.class, resolver, (context, line) -> {
                    final var entity = context.getArgument("entity", EntityType.class);
                    line.setEntityType(entity);
                    return true;
                }))
                .then(setType("item", ArgumentTypes.itemStack(), plugin, ItemHologramLine.class, resolver, (context, line) -> {
                    final var item = context.getArgument("item", ItemStack.class);
                    line.setItemStack(item);
                    return true;
                }))
                .then(setType("text", StringArgumentType.greedyString(), plugin, TextHologramLine.class, resolver, (context, line) -> {
                    final var text = context.getArgument("text", String.class);
                    line.setUnparsedText(text);
                    return true;
                }));
    }

    private static <T extends HologramLine> LiteralArgumentBuilder<CommandSourceStack> setType(
            final String name,
            final ArgumentType<?> argumentType,
            final HologramPlugin plugin,
            final Class<T> lineType,
            final LineTargetResolver resolver,
            final SetHandler<T> handler
    ) {
        return Commands.literal(name).then(Commands.argument(name, argumentType).executes(context -> {
            final var result = resolver.resolve(context, plugin).orElse(null);
            if (result == null) return 0;

            var line = result.line();
            if (!lineType.isInstance(line)) {
                if (lineType.equals(BlockHologramLine.class)) {
                    line = result.hologram().setBlockLine(result.lineIndex());
                } else if (lineType.equals(EntityHologramLine.class)) {
                    line = result.hologram().setEntityLine(result.lineIndex(), EntityType.ACACIA_BOAT);
                } else if (lineType.equals(ItemHologramLine.class)) {
                    line = result.hologram().setItemLine(result.lineIndex());
                } else if (lineType.equals(TextHologramLine.class)) {
                    line = result.hologram().setTextLine(result.lineIndex());
                } else if (lineType.equals(PagedHologramLine.class)) {
                    line = result.hologram().setPagedLine(result.lineIndex());
                } else throw new IllegalArgumentException("Invalid line type: " + lineType);
            }

            if (handler.apply(context, lineType.cast(line))) {
                plugin.bundle().sendMessage(context.getSource().getSender(),
                        result.isPage() ? "hologram.page.set" : "hologram.line.set",
                        buildPlaceholders(result));
                return SINGLE_SUCCESS;
            } else {
                plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.type.not.paged",
                        buildPlaceholders(result));
                return 0;
            }
        }));
    }

    @FunctionalInterface
    private interface SetHandler<T extends HologramLine> {
        boolean apply(CommandContext<CommandSourceStack> context, T line);
    }

    private static <T extends HologramLine> int editTyped(
            final CommandContext<CommandSourceStack> context,
            final HologramPlugin plugin,
            final LineTargetResolver resolver,
            final Class<T> type,
            final String wrongTypeKey,
            final Consumer<T> change,
            final String successKey
    ) {
        final var target = resolver.resolve(context, plugin);
        if (target.isEmpty()) return 0;

        final var result = target.get();
        if (!type.isInstance(result.line())) {
            plugin.bundle().sendMessage(context.getSource().getSender(), wrongTypeKey, buildPlaceholders(result));
            return 0;
        }

        change.accept(type.cast(result.line()));
        plugin.bundle().sendMessage(context.getSource().getSender(), successKey, buildPlaceholders(result));
        return SINGLE_SUCCESS;
    }

    private static TagResolver[] buildPlaceholders(final LineEditTarget target) {
        final var list = new ArrayList<TagResolver>();
        list.add(Placeholder.unparsed("hologram", target.hologram().getName()));
        list.add(Formatter.number("line", target.displayLineIndex()));
        if (target.isPage()) {
            list.add(Formatter.number("page", target.displayPageIndex()));
        }
        return list.toArray(TagResolver[]::new);
    }

    private static <T> Optional<T> tryGetArgument(final CommandContext<CommandSourceStack> context, final String name, final Class<T> type) {
        try {
            return Optional.of(context.getArgument(name, type));
        } catch (final IllegalArgumentException e) {
            if (e.getMessage().equals("No such argument '" + name + "' exists on this command"))
                return Optional.empty();
            throw e;
        }
    }

    public static <T extends ArgumentBuilder<CommandSourceStack, T>> T addAllEditCommands(
            final T builder,
            final HologramPlugin plugin,
            final LineTargetResolver resolver
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
