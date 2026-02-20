package net.thenextlvl.hologram.models.line;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.StaticHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.hologram.locale.ImageTagResolver;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Color;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public final class PaperTextHologramLine extends PaperDisplayHologramLine<TextDisplay> implements TextHologramLine {
    private volatile @Nullable Color backgroundColor;
    private volatile @Nullable String unparsedText = null;
    private volatile TextDisplay.TextAlignment alignment = TextDisplay.TextAlignment.CENTER;
    private volatile boolean defaultBackground = false;
    private volatile boolean seeThrough = false;
    private volatile boolean shadowed = false;
    private volatile int opacity = 0;
    private volatile int lineWidth = Integer.MAX_VALUE;

    public PaperTextHologramLine(final PaperHologram hologram, @Nullable final PagedHologramLine parentLine) {
        super(hologram, parentLine, EntityType.TEXT_DISPLAY);
    }

    @Override
    public LineType getType() {
        return LineType.TEXT;
    }

    @Override
    public Optional<Component> getText(final Player player) {
        return getUnparsedText().map(string -> parse(getHologram().getPlugin(), getHologram(), this, string, player));
    }

    public static Component parse(final HologramPlugin plugin, final PaperHologram hologram, final HologramLine line, final String text, final Player player) {
        final var translated = plugin.translations().translate(player, text, 0);

        final var papiFormatter = plugin.papiFormatter;
        final var formatted = papiFormatter != null ? papiFormatter.format(player, translated) : translated;

        final var builder = TagResolver.builder();

        builder.resolver(StandardTags.defaults());
        builder.resolver(ImageTagResolver.INSTANCE);

        final var miniFormatter = plugin.miniFormatter;
        if (miniFormatter != null) builder.resolver(miniFormatter.tagResolver());

        final var online = hologram.getPlugin().getServer().getOnlinePlayers().size();
        builder.tag("hologram", Tag.preProcessParsed(hologram.getName()));
        builder.tag("lines", Tag.inserting(Component.text(hologram.getLineCount())));
        builder.tag("player", Tag.preProcessParsed(player.getName()));
        builder.tag("players", Tag.inserting(Component.text(online)));

        final var parentLine = line instanceof final StaticHologramLine staticLine
                ? staticLine.getParentLine() : Optional.<PagedHologramLine>empty();
        parentLine.ifPresentOrElse(parent -> {
            builder.tag("line", Tag.inserting(Component.text(hologram.getLineIndex(parent) + 1)));
            final var pageCount = parent.getPages()
                    .filter(page -> page.canSee(player))
                    .count();
            final var visiblePageIndex = parent.getPages()
                    .limit(parent.getPageIndex(line))
                    .filter(page -> page.canSee(player))
                    .count();
            builder.tag("page", Tag.inserting(Component.text(visiblePageIndex + 1)));
            builder.tag("pages", Tag.inserting(Component.text(pageCount)));
        }, () -> builder.tag("line", Tag.inserting(Component.text(hologram.getLineIndex(line) + 1))));

        return MiniMessage.miniMessage().deserialize(formatted, player, builder.build());
    }

    @Override
    public Optional<String> getUnparsedText() {
        return Optional.ofNullable(unparsedText);
    }

    @Override
    public boolean setText(@Nullable final Component text) {
        return setUnparsedText(text != null ? MiniMessage.miniMessage().serialize(text) : null);
    }

    @Override
    public boolean setUnparsedText(@Nullable final String text) {
        return set(this.unparsedText, text, () -> this.unparsedText = text, true);
    }

    @Override
    public int getLineWidth() {
        return lineWidth;
    }

    @Override
    public boolean setLineWidth(final int width) {
        return set(this.lineWidth, width, () -> {
            this.lineWidth = width;
            forEachEntity(entity -> entity.setLineWidth(width));
        }, false);
    }

    @Override
    public Optional<Color> getBackgroundColor() {
        return Optional.ofNullable(backgroundColor);
    }

    @Override
    public boolean setBackgroundColor(@Nullable final Color color) {
        return set(this.backgroundColor, color, () -> {
            this.backgroundColor = color;
            forEachEntity(entity -> entity.setBackgroundColor(color));
        }, false);
    }

    @Override
    public int getTextOpacity() {
        return opacity;
    }

    @Override
    public boolean setTextOpacity(@Range(from = 0, to = 100) final int opacity) {
        return set(this.opacity, opacity, () -> {
            this.opacity = opacity;
            forEachEntity(this::updateOpacity);
        }, false);
    }

    @Override
    public boolean isShadowed() {
        return shadowed;
    }

    @Override
    public boolean setShadowed(final boolean shadow) {
        return set(this.shadowed, shadow, () -> {
            this.shadowed = shadow;
            forEachEntity(entity -> entity.setShadowed(shadow));
        }, false);
    }

    @Override
    public boolean isSeeThrough() {
        return seeThrough;
    }

    @Override
    public boolean setSeeThrough(final boolean seeThrough) {
        return set(this.seeThrough, seeThrough, () -> {
            this.seeThrough = seeThrough;
            forEachEntity(entity -> entity.setSeeThrough(seeThrough));
        }, false);
    }

    @Override
    public boolean isDefaultBackground() {
        return defaultBackground;
    }

    @Override
    public boolean setDefaultBackground(final boolean defaultBackground) {
        return set(this.defaultBackground, defaultBackground, () -> {
            this.defaultBackground = defaultBackground;
            forEachEntity(entity -> entity.setDefaultBackground(defaultBackground));
        }, false);
    }

    @Override
    public TextDisplay.TextAlignment getAlignment() {
        return alignment;
    }

    @Override
    public boolean setAlignment(final TextDisplay.TextAlignment alignment) {
        return set(this.alignment, alignment, () -> {
            this.alignment = alignment;
            forEachEntity(entity -> entity.setAlignment(alignment));
        }, false);
    }

    @Override
    public double getHeight(final Player player) {
        return (0.25 * transformation.getScale().y()) * getText(player)
                .map(MiniMessage.miniMessage()::serialize)
                .map(s -> s.replaceAll("<br>|<newline>", "\n"))
                .map(s -> s.chars().filter(c -> c == '\n').count() + 1)
                .orElse(1L);
    }

    @Override
    protected void preSpawn(final TextDisplay entity, final Player player) {
        updateText(player, entity);
        entity.setAlignment(alignment);
        entity.setSeeThrough(seeThrough);
        entity.setShadowed(shadowed);
        entity.setDefaultBackground(defaultBackground);
        entity.setBackgroundColor(backgroundColor);
        entity.setLineWidth(lineWidth);
        updateOpacity(entity);

        super.preSpawn(entity, player);
    }

    public void updateText(final Player player, final TextDisplay entity) {
        getText(player).ifPresentOrElse(component -> {
            getHologram().getPlugin().supply(entity, () -> {
                entity.text(Component.empty());
                entity.text(component);
            });
        }, () -> getHologram().getPlugin().supply(entity, () -> entity.text(null)));
    }

    public void updateText() {
        entities.forEach((uuid, entity) -> {
            final var player = getHologram().getPlugin().getServer().getPlayer(uuid);
            if (player != null) updateText(player, entity);
        });
    }

    private void updateOpacity(final TextDisplay entity) {
        entity.setTextOpacity((byte) Math.round(25f + ((100f - opacity) * 2.3f)));
    }

    @Override
    public HologramLine copyFrom(final HologramLine other) {
        if (other instanceof final TextHologramLine line) {
            backgroundColor = line.getBackgroundColor().orElse(null);
            unparsedText = line.getUnparsedText().orElse(null);
            alignment = line.getAlignment();
            defaultBackground = line.isDefaultBackground();
            seeThrough = line.isSeeThrough();
            shadowed = line.isShadowed();
            opacity = line.getTextOpacity();
            lineWidth = line.getLineWidth();
        }
        return super.copyFrom(other);
    }
}
