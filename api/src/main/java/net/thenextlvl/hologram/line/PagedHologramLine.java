package net.thenextlvl.hologram.line;

import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Represents a hologram line that cycles through multiple pages.
 * Each page can be of any line type (text, item, block, entity),
 * allowing for animations, GIF-like effects, and dynamic content.
 *
 * @since 0.5.0
 */
@ApiStatus.NonExtendable
public interface PagedHologramLine extends HologramLine {
    /**
     * Gets all pages of this paged line.
     *
     * @return an unmodifiable list of all pages
     * @since 0.5.0
     */
    @Contract(pure = true)
    @Unmodifiable
    List<HologramLine> getPages();

    /**
     * Gets the page at the given index.
     *
     * @param index page index
     * @return the page at the given index
     * @since 0.5.0
     */
    @Contract(pure = true)
    Optional<HologramLine> getPage(int index);

    /**
     * Gets the page at the given index if it is of the given type.
     *
     * @param index page index
     * @param type  page type
     * @param <T>   page type
     * @return the page at the given index of the given type
     * @since 0.5.0
     */
    @Contract(pure = true)
    <T extends HologramLine> Optional<T> getPage(int index, Class<T> type);

    /**
     * Gets the number of pages in this paged line.
     *
     * @return number of pages
     * @since 0.5.0
     */
    @Contract(pure = true)
    int getPageCount();

    /**
     * Adds a text page to this paged line.
     *
     * @return the newly created text page
     * @since 0.5.0
     */
    @Contract(value = " -> new", mutates = "this")
    TextHologramLine addTextPage();

    /**
     * Adds an item page to this paged line.
     *
     * @return the newly created item page
     * @since 0.5.0
     */
    @Contract(value = " -> new", mutates = "this")
    ItemHologramLine addItemPage();

    /**
     * Adds a block page to this paged line.
     *
     * @return the newly created block page
     * @since 0.5.0
     */
    @Contract(value = " -> new", mutates = "this")
    BlockHologramLine addBlockPage();

    /**
     * Adds an entity page to this paged line.
     *
     * @param entityType the type of entity to display
     * @return the newly created entity page
     * @throws IllegalArgumentException if the entity type is not spawnable
     * @since 0.5.0
     */
    @Contract(value = "_ -> new", mutates = "this")
    EntityHologramLine addEntityPage(EntityType entityType) throws IllegalArgumentException;

    /**
     * Removes the page at the given index.
     *
     * @param index page index
     * @return {@code true} if the page was removed, {@code false} otherwise
     * @since 0.5.0
     */
    @Contract(mutates = "this")
    boolean removePage(int index);

    /**
     * Removes the given page from this paged line.
     *
     * @param page the page to remove
     * @return {@code true} if the page was removed, {@code false} otherwise
     * @since 0.5.0
     */
    @Contract(mutates = "this")
    boolean removePage(HologramLine page);

    /**
     * Removes all pages from this paged line.
     *
     * @since 0.5.0
     */
    @Contract(mutates = "this")
    void clearPages();

    /**
     * Gets the interval between page changes.
     *
     * @return the interval duration
     * @since 0.5.0
     */
    @Contract(pure = true)
    Duration getInterval();

    /**
     * Sets the interval between page changes.
     *
     * @param interval the new interval duration
     * @return this
     * @since 0.5.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    PagedHologramLine setInterval(Duration interval);

    /**
     * Gets whether pages are cycled in random order.
     *
     * @return {@code true} if pages are cycled randomly, {@code false} for sequential order
     * @since 0.5.0
     */
    @Contract(pure = true)
    boolean isRandomOrder();

    /**
     * Sets whether pages should be cycled in random order.
     *
     * @param random {@code true} for random order, {@code false} for sequential
     * @return this
     * @since 0.5.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    PagedHologramLine setRandomOrder(boolean random);

    /**
     * Gets whether the paged line is currently paused.
     *
     * @return {@code true} if paused, {@code false} otherwise
     * @since 0.5.0
     */
    @Contract(pure = true)
    boolean isPaused();

    /**
     * Sets whether the paged line should be paused.
     * When paused, the line will not automatically cycle through pages.
     *
     * @param paused {@code true} to pause, {@code false} to resume
     * @return this
     * @since 0.5.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    PagedHologramLine setPaused(boolean paused);
}
