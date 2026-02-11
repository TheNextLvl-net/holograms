package net.thenextlvl.hologram.action;

import net.thenextlvl.hologram.line.HologramLine;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@NullMarked
final class SimpleClickAction<T> implements ClickAction<T> {
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    private final ActionType<T> actionType;
    private EnumSet<ClickType> clickTypes;
    private T input;

    private @Range(from = 0, to = 100) int chance = 100;
    private Duration cooldown = Duration.ZERO;
    private @Nullable String permission = null;
    private double cost = 0;

    public SimpleClickAction(final ActionType<T> actionType, final EnumSet<ClickType> clickTypes, final T input) {
        this.actionType = actionType;
        this.clickTypes = clickTypes;
        this.input = input;
    }

    @Override
    public ActionType<T> getActionType() {
        return actionType;
    }

    @Override
    public EnumSet<ClickType> getClickTypes() {
        return clickTypes;
    }

    @Override
    public boolean setClickTypes(final EnumSet<ClickType> clickTypes) {
        if (Objects.equals(this.clickTypes, clickTypes)) return false;
        this.clickTypes = clickTypes;
        return true;
    }

    @Override
    public boolean isSupportedClickType(final ClickType type) {
        return clickTypes.contains(type);
    }

    @Override
    public T getInput() {
        return input;
    }

    @Override
    public boolean setInput(final T input) {
        if (Objects.equals(this.input, input)) return false;
        this.input = input;
        return true;
    }

    @Override
    public @Range(from = 0, to = 100) int getChance() {
        return chance;
    }

    @Override
    public boolean setChance(@Range(from = 0, to = 100) final int chance) {
        if (this.chance == chance) return false;
        this.chance = Math.clamp(chance, 0, 100);
        return true;
    }

    @Override
    public Optional<String> getPermission() {
        return Optional.ofNullable(permission);
    }

    @Override
    public boolean setPermission(@Nullable final String permission) {
        if (Objects.equals(this.permission, permission)) return false;
        this.permission = permission;
        return true;
    }

    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public boolean setCost(final double cost) {
        if (this.cost == cost) return false;
        this.cost = cost;
        return true;
    }

    @Override
    public Duration getCooldown() {
        return cooldown;
    }

    @Override
    public boolean setCooldown(final Duration cooldown) {
        if (Objects.equals(this.cooldown, cooldown)) return false;
        this.cooldown = cooldown;
        return true;
    }

    @Override
    public boolean isOnCooldown(final Player player) {
        return cooldown.isPositive() && cooldowns.computeIfPresent(player.getUniqueId(), (ignored, lastUsed) -> {
            if (System.currentTimeMillis() - cooldown.toMillis() > lastUsed) return null;
            return lastUsed;
        }) != null;
    }

    @Override
    public boolean resetCooldown(final Player player) {
        return cooldowns.remove(player.getUniqueId()) != null;
    }

    @Override
    public boolean canInvoke(final Player player) {
        return (permission == null || player.hasPermission(permission)) && !isOnCooldown(player);
    }

    @Override
    public boolean invoke(final HologramLine line, final Player player) {
        if (!canInvoke(player)) return false;
        if (cooldown.isPositive()) cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        if (ThreadLocalRandom.current().nextInt(100) > chance) return false;
        // todo: withdraw cost
        actionType.action().invoke(line, player, input);
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final SimpleClickAction<?> that = (SimpleClickAction<?>) o;
        return chance == that.chance
                && Objects.equals(actionType, that.actionType)
                && Objects.equals(clickTypes, that.clickTypes)
                && Objects.equals(input, that.input)
                && Objects.equals(cooldown, that.cooldown)
                && Objects.equals(permission, that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionType, clickTypes, input, chance, cooldown, permission);
    }
}
