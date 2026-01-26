package net.thenextlvl.hologram.models.line;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.hologram.line.StaticHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

@NullMarked
public abstract class PaperStaticHologramLine<E extends Entity> extends PaperHologramLine<E> implements StaticHologramLine {
    protected volatile @Nullable TextColor glowColor = null;
    protected volatile boolean glowing = false;

    public PaperStaticHologramLine(final PaperHologram hologram, final Class<E> entityClass) {
        super(hologram, entityClass);
    }

    @Override
    public Optional<TextColor> getGlowColor() {
        return Optional.ofNullable(glowColor);
    }

    @Override
    public StaticHologramLine setGlowColor(@Nullable final TextColor color) {
        if (Objects.equals(this.glowColor, color)) return this;
        this.glowColor = color;
        updateGlowColor(color);
        return this;
    }

    @Override
    public boolean isGlowing() {
        return glowing;
    }

    @Override
    public StaticHologramLine setGlowing(final boolean glowing) {
        if (glowing == this.glowing) return this;
        this.glowing = glowing;
        getEntities().values().forEach(entity -> entity.setGlowing(glowing));
        return this;
    }

    @Override
    protected void updateTeamOptions(final Team team) {
        team.color(getGlowColor().map(NamedTextColor::nearestTo).orElse(null));
    }

    protected abstract void updateGlowColor(@Nullable final TextColor color);

    @Override
    protected void preSpawn(final E entity, final Player player) {
        super.preSpawn(entity, player);
        entity.setGlowing(glowing);
    }
}
