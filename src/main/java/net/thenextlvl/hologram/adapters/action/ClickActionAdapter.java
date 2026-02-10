package net.thenextlvl.hologram.adapters.action;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.title.Title;
import net.thenextlvl.hologram.action.ActionType;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.action.ClickType;
import net.thenextlvl.hologram.action.UnparsedTitle;
import net.thenextlvl.nbt.serialization.NBT;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.serialization.adapters.EnumAdapter;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.ListTag;
import net.thenextlvl.nbt.tag.StringTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.Location;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;
import java.util.EnumSet;
import java.util.stream.Collectors;

@NullMarked
public final class ClickActionAdapter implements TagAdapter<ClickAction<?>> {
    private final NBT context = NBT.builder()
            .registerTypeHierarchyAdapter(ActionType.class, new ActionTypeAdapter())
            .registerTypeHierarchyAdapter(ClickType.class, new EnumAdapter<>(ClickType.class))
            .registerTypeHierarchyAdapter(Key.class, new KeyAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LazyLocationAdapter())
            .registerTypeHierarchyAdapter(Sound.Source.class, new EnumAdapter<>(Sound.Source.class))
            .registerTypeHierarchyAdapter(Sound.class, new SoundAdapter())
            .registerTypeHierarchyAdapter(Title.Times.class, new TitleTimesAdapter())
            .registerTypeHierarchyAdapter(UnparsedTitle.class, new UnparsedTitleAdapter())
            .build();

    @Override
    @SuppressWarnings("unchecked")
    public ClickAction<?> deserialize(final Tag tag, final TagDeserializationContext ignored) throws ParserException {
        final var root = tag.getAsCompound();
        final var permission = root.optional("permission").map(Tag::getAsString).orElse(null);
        final var cooldown = root.optional("cooldown").map(Tag::getAsLong).map(Duration::ofMillis).orElse(Duration.ZERO);
        final var actionType = context.deserialize(root.get("actionType"), ActionType.class);
        final var clickTypes = root.getAsList("clickTypes").stream()
                .map(tag1 -> context.deserialize(tag1, ClickType.class))
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(ClickType.class)));
        final var input = context.deserialize(root.get("input"), actionType.type());
        final var chance = root.optional("chance").map(Tag::getAsInt).orElse(100);
        final var action = ClickAction.create(actionType, clickTypes, input);
        action.setChance(chance);
        action.setCooldown(cooldown);
        action.setPermission(permission);
        return action;
    }

    @Override
    public Tag serialize(final ClickAction<?> action, final TagSerializationContext ignored) throws ParserException {
        final var builder = CompoundTag.builder();
        action.getPermission().ifPresent(permission -> builder.put("permission", permission));
        if (!action.getCooldown().isZero()) builder.put("cooldown", action.getCooldown().toMillis());
        builder.put("actionType", context.serialize(action.getActionType()));
        final var types = ListTag.builder().contentType(StringTag.ID);
        action.getClickTypes().forEach(clickType -> types.add(context.serialize(clickType)));
        builder.put("clickTypes", types.build());
        builder.put("input", context.serialize(action.getInput()));
        builder.put("chance", action.getChance());
        return builder.build();
    }
}
