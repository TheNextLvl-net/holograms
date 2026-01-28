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
    private final HologramPlugin plugin;
    private final LineTargetResolver resolver;

    public EditCommands(final HologramPlugin plugin, final LineTargetResolver resolver) {
        this.plugin = plugin;
        this.resolver = resolver;
    }


    public LiteralArgumentBuilder<CommandSourceStack> alignment() {
        return create("alignment")
                .textType()
                .enumArg(TextAlignment.class, TextHologramLine::setAlignment)
                .successMessage("hologram.text-alignment")
                .build();
    }

    public LiteralArgumentBuilder<CommandSourceStack> append() {
        return create("append")
                .textType()
                .textArg((line, text) -> line.getUnparsedText().map(s -> s.concat(text)).ifPresent(line::setUnparsedText))
                .successMessage("hologram.text.set")
                .build();
    }

    public LiteralArgumentBuilder<CommandSourceStack> prepend() {
        return create("prepend")
                .textType()
                .textArg((line, text) -> line.getUnparsedText().map(text::concat).ifPresent(line::setUnparsedText))
                .successMessage("hologram.text.set")
                .build();
    }

    public LiteralArgumentBuilder<CommandSourceStack> replace() {
        final var matchArg = Commands.argument("match", StringArgumentType.string());
        final var replacementArg = Commands.argument("replacement", StringArgumentType.greedyString());
        return Commands.literal("replace")
                .requires(requiresPermission("replace"))
                .then(matchArg.then(replacementArg.executes(context -> editTyped(context, TextHologramLine.class, "hologram.type.text", line -> {
                    final var match = context.getArgument("match", String.class);
                    final var replacement = context.getArgument("replacement", String.class);
                    line.getUnparsedText().map(s -> s.replace(match, replacement)).ifPresent(line::setUnparsedText);
                }, "hologram.text.set"))));
    }

    public LiteralArgumentBuilder<CommandSourceStack> backgroundColor() {
        return create("background-color")
                .textType()
                .reset(line -> line.setBackgroundColor(null), "hologram.background-color.reset")
                .arg("color", new ColorArgumentType(), Color.class, TextHologramLine::setBackgroundColor)
                .successMessage("hologram.background-color")
                .build();
    }

    public LiteralArgumentBuilder<CommandSourceStack> billboard() {
        return create("billboard")
                .displayType()
                .enumArg(Billboard.class, DisplayHologramLine::setBillboard)
                .successMessage("hologram.billboard")
                .build();
    }

    public LiteralArgumentBuilder<CommandSourceStack> brightness() {
        final var brightnessArg = Commands.argument("brightness", IntegerArgumentType.integer(0, 15));
        final var blockArg = Commands.argument("block", IntegerArgumentType.integer(0, 15));
        final var skyArg = Commands.argument("sky", IntegerArgumentType.integer(0, 15));
        return Commands.literal("brightness")
                .requires(requiresPermission("brightness"))
                .then(Commands.literal("reset").executes(context -> editTyped(context, DisplayHologramLine.class, "hologram.type.display",
                        line -> line.setBrightness(null), "hologram.brightness.reset")))
                .then(brightnessArg.executes(context -> editTyped(context, DisplayHologramLine.class, "hologram.type.display", line -> {
                    final var brightness = context.getArgument("brightness", int.class);
                    line.setBrightness(new Display.Brightness(brightness, brightness));
                }, "hologram.brightness")))
                .then(blockArg.then(skyArg.executes(context -> editTyped(context, DisplayHologramLine.class, "hologram.type.display", line -> {
                    final var block = context.getArgument("block", int.class);
                    final var sky = context.getArgument("sky", int.class);
                    line.setBrightness(new Display.Brightness(block, sky));
                }, "hologram.brightness"))));
    }

    public LiteralArgumentBuilder<CommandSourceStack> defaultBackground() {
        return create("default-background")
                .textType()
                .boolArg(TextHologramLine::setDefaultBackground)
                .successMessage("hologram.default-background")
                .build();
    }

    public LiteralArgumentBuilder<CommandSourceStack> glowColor() {
        return create("glow-color")
                .typed(StaticHologramLine.class, "hologram.type.single")
                .reset(line -> line.setGlowColor(null), "hologram.line.glow-color.reset")
                .arg("color", ArgumentTypes.namedColor(), NamedTextColor.class, StaticHologramLine::setGlowColor)
                .arg("hex", ArgumentTypes.hexColor(), TextColor.class, StaticHologramLine::setGlowColor)
                .successMessage("hologram.line.glow-color")
                .build();
    }

    public LiteralArgumentBuilder<CommandSourceStack> glowing() {
        final var glowingArg = Commands.argument("glowing", BoolArgumentType.bool());
        return Commands.literal("glowing")
                .requires(requiresPermission("glowing"))
                .then(glowingArg.executes(context -> {
                    final var glowing = context.getArgument("glowing", boolean.class);
                    return editTyped(context, StaticHologramLine.class, "hologram.type.single",
                            line -> line.setGlowing(glowing),
                            glowing ? "hologram.line.glow.enabled" : "hologram.line.glow.disabled");
                }));
    }

    public LiteralArgumentBuilder<CommandSourceStack> interpolationDelay() {
        return create("interpolation-delay")
                .displayType()
                .intArg("delay", 0, DisplayHologramLine::setInterpolationDelay)
                .successMessage("hologram.interpolation-delay")
                .build();
    }

    public LiteralArgumentBuilder<CommandSourceStack> interpolationDuration() {
        return create("interpolation-duration")
                .displayType()
                .intArg("duration", 0, DisplayHologramLine::setInterpolationDuration)
                .successMessage("hologram.interpolation-duration")
                .build();
    }

    public LiteralArgumentBuilder<CommandSourceStack> offset() {
        return Commands.literal("offset")
                .requires(requiresPermission("offset"))
                .then(Commands.literal("reset").executes(context -> editForOffset(context, new Vector3f())))
                .then(vector3fArguments(-16, 16, this::editForOffset));
    }

    private int editForOffset(final CommandContext<CommandSourceStack> context, final Vector3f offset) {
        return editDisplayOrEntity(context, "hologram.offset",
                displayLine -> displayLine.getTransformation().getTranslation().set(offset),
                entityLine -> entityLine.setOffset(offset));
    }

    public LiteralArgumentBuilder<CommandSourceStack> opacity() {
        return create("opacity")
                .textType()
                .arg("opacity", FloatArgumentType.floatArg(0, 100), float.class, TextHologramLine::setTextOpacity)
                .successMessage("hologram.opacity")
                .build();
    }

    public LiteralArgumentBuilder<CommandSourceStack> scale() {
        final var scaleArg = Commands.argument("scale", FloatArgumentType.floatArg(0.1f));
        return Commands.literal("scale")
                .requires(requiresPermission("scale"))
                .then(scaleArg.executes(this::editForScale))
                .then(vector3fArguments(0.1f, 100, (context, vector) -> editForScale(context)));
    }

    private int editForScale(final CommandContext<CommandSourceStack> context) {
        final var scale = tryGetArgument(context, "scale", float.class)
                .map(Vector3f::new).orElseGet(() -> getVector3f(context));

        return editDisplayOrEntity(context, "hologram.scale", displayLine -> {
            final var transformation = displayLine.getTransformation();
            transformation.getScale().set(scale);
            displayLine.setTransformation(transformation);
        }, entityLine -> entityLine.setScale(scale.y()));
    }

    public LiteralArgumentBuilder<CommandSourceStack> seeThrough() {
        return create("see-through")
                .textType()
                .boolArg(TextHologramLine::setSeeThrough)
                .successMessage("hologram.see-through")
                .build();
    }

    public LiteralArgumentBuilder<CommandSourceStack> shadowed() {
        return create("shadowed")
                .textType()
                .boolArg(TextHologramLine::setShadowed)
                .successMessage("hologram.shadowed")
                .build();
    }

    public LiteralArgumentBuilder<CommandSourceStack> teleportDuration() {
        return create("teleport-duration")
                .displayType()
                .intArg("duration", 0, DisplayHologramLine::setTeleportDuration)
                .successMessage("hologram.teleport-duration")
                .build();
    }

    public LiteralArgumentBuilder<CommandSourceStack> transformation() {
        return create("transformation")
                .typed(ItemHologramLine.class, "hologram.type.item")
                .enumArg(ItemDisplay.ItemDisplayTransform.class, ItemHologramLine::setItemDisplayTransform)
                .successMessage("hologram.transformation")
                .build();
    }

    private EditBuilder<HologramLine> create(final String name) {
        return new EditBuilder<>(name, HologramLine.class, "hologram.type.line");
    }

    private final class EditBuilder<T extends HologramLine> {
        private final String name;
        private final Class<T> lineType;
        private final String wrongTypeKey;
        private final LiteralArgumentBuilder<CommandSourceStack> builder;
        private @Nullable String successKey = null;

        private EditBuilder(final String name, final Class<T> lineType, final String wrongTypeKey) {
            this.name = name;
            this.lineType = lineType;
            this.wrongTypeKey = wrongTypeKey;
            this.builder = Commands.literal(name).requires(requiresPermission(name));
        }

        private <U extends HologramLine> EditBuilder<U> typed(final Class<U> type, final String wrongTypeKey) {
            return new EditBuilder<>(name, type, wrongTypeKey);
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
                return editTyped(context, lineType, wrongTypeKey, line -> {
                    setter.accept(line, context.getArgument(argName, valueType));
                }, Objects.requireNonNull(successKey, "successKey cannot be null"));
            }));
            return this;
        }

        EditBuilder<T> reset(final Consumer<T> resetAction, final String resetSuccessKey) {
            builder.then(Commands.literal("reset").executes(context -> {
                return editTyped(context, lineType, wrongTypeKey, resetAction, resetSuccessKey);
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

    private static ArgumentBuilder<CommandSourceStack, ?> vector3fArguments(
            final float min, final float max,
            final BiFunction<CommandContext<CommandSourceStack>, Vector3f, Integer> handler
    ) {
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

    private int editDisplayOrEntity(
            final CommandContext<CommandSourceStack> context,
            final String successKey,
            final Consumer<DisplayHologramLine> displayAction,
            final Consumer<EntityHologramLine> entityAction
    ) {
        final var target = resolver.resolve(context, plugin);
        if (target == null) return 0;
        final var line = target.line();

        if (line instanceof final DisplayHologramLine displayLine) {
            displayAction.accept(displayLine);
        } else if (line instanceof final EntityHologramLine entityLine) {
            entityAction.accept(entityLine);
        } else {
            plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.type.display", buildPlaceholders(target));
            return 0;
        }

        plugin.bundle().sendMessage(context.getSource().getSender(), successKey, buildPlaceholders(target));
        return SINGLE_SUCCESS;
    }

    public LiteralArgumentBuilder<CommandSourceStack> set() {
        return Commands.literal("set")
                .requires(source -> source.getSender().hasPermission("holograms.command.edit.set"))
                .then(setType("block", ArgumentTypes.blockState(), BlockHologramLine.class, (context, line) -> {
                    final var block = context.getArgument("block", BlockState.class).getBlockData();
                    line.setBlock(block);
                    return true;
                }))
                .then(setType("entity", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE), EntityHologramLine.class, (context, line) -> {
                    final var entity = context.getArgument("entity", EntityType.class);
                    line.setEntityType(entity);
                    return true;
                }))
                .then(setType("item", ArgumentTypes.itemStack(), ItemHologramLine.class, (context, line) -> {
                    final var item = context.getArgument("item", ItemStack.class);
                    line.setItemStack(item);
                    return true;
                }))
                .then(setType("text", StringArgumentType.greedyString(), TextHologramLine.class, (context, line) -> {
                    final var text = context.getArgument("text", String.class);
                    line.setUnparsedText(text);
                    return true;
                }));
    }

    private <T extends HologramLine> LiteralArgumentBuilder<CommandSourceStack> setType(
            final String name,
            final ArgumentType<?> argumentType,
            final Class<T> lineType,
            final SetHandler<T> handler
    ) {
        return Commands.literal(name).then(Commands.argument(name, argumentType).executes(context -> {
            final var result = resolver.resolve(context, plugin);
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

    private <T extends HologramLine> int editTyped(
            final CommandContext<CommandSourceStack> context,
            final Class<T> type,
            final String wrongTypeKey,
            final Consumer<T> change,
            final String successKey
    ) {
        final var target = resolver.resolve(context, plugin);
        if (target == null) return 0;

        if (!type.isInstance(target.line())) {
            plugin.bundle().sendMessage(context.getSource().getSender(), wrongTypeKey, buildPlaceholders(target));
            return 0;
        }

        change.accept(type.cast(target.line()));
        plugin.bundle().sendMessage(context.getSource().getSender(), successKey, buildPlaceholders(target));
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
        final var command = new EditCommands(plugin, resolver);
        return builder
                .then(command.alignment())
                .then(command.append())
                .then(command.backgroundColor())
                .then(command.billboard())
                .then(command.brightness())
                .then(command.defaultBackground())
                .then(command.glowColor())
                .then(command.glowing())
                .then(command.interpolationDelay())
                .then(command.interpolationDuration())
                .then(command.offset())
                .then(command.opacity())
                .then(command.prepend())
                .then(command.replace())
                .then(command.scale())
                .then(command.seeThrough())
                .then(command.set())
                .then(command.shadowed())
                .then(command.teleportDuration())
                .then(command.transformation());
    }
}
