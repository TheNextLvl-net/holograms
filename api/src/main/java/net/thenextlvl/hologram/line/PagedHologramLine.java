package net.thenextlvl.hologram.line;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.time.Duration;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
     * @return a stream of all pages
     * @since 0.9.0
     */
    @Contract(pure = true)
    Stream<StaticHologramLine> getPages();

    /**
     * Gets the page at the given index.
     *
     * @param index page index
     * @return the page at the given index
     * @since 0.5.0
     */
    @Contract(pure = true)
    Optional<StaticHologramLine> getPage(int index);

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
    <T extends StaticHologramLine> Optional<T> getPage(int index, Class<T> type);

    /**
     * Gets the number of pages in this paged line.
     *
     * @return number of pages
     * @since 0.5.0
     */
    @Contract(pure = true)
    int getPageCount();

    /**
     * Gets the index of the given page.
     *
     * @param line the page to find the index of
     * @return the index of the page, or -1 if not found
     * @since 0.7.0
     */
    @Contract(pure = true)
    int getPageIndex(HologramLine line);

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
     * Swaps the pages at the given indices.
     *
     * @param first  the index of the first page
     * @param second the index of the second page
     * @return {@code true} if the pages were swapped, {@code false} otherwise
     * @since 0.6.0
     */
    @Contract(mutates = "this")
    boolean swapPages(int first, int second);

    /**
     * Moves a page from one index to another.
     *
     * @param from the current index of the page
     * @param to   the target index for the page
     * @return {@code true} if the page was moved, {@code false} otherwise
     * @since 0.6.0
     */
    @Contract(mutates = "this")
    boolean movePage(int from, int to);

    /**
     * Sets the text page at the specified index.
     *
     * @param index the index at which to set the page
     * @return the newly created text page
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @since 0.7.0
     */
    @Contract(value = "_ -> new", mutates = "this")
    TextHologramLine setTextPage(int index) throws IndexOutOfBoundsException;

    /**
     * Sets the item page at the specified index.
     *
     * @param index the index at which to set the page
     * @return the newly created item page
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @since 0.7.0
     */
    @Contract(value = "_ -> new", mutates = "this")
    ItemHologramLine setItemPage(int index) throws IndexOutOfBoundsException;

    /**
     * Sets the block page at the specified index.
     *
     * @param index the index at which to set the page
     * @return the newly created block page
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @since 0.7.0
     */
    @Contract(value = "_ -> new", mutates = "this")
    BlockHologramLine setBlockPage(int index) throws IndexOutOfBoundsException;

    /**
     * Sets the entity page at the specified index.
     *
     * @param index      the index at which to set the page
     * @param entityType the type of entity to display
     * @return the newly created entity page
     * @throws IllegalArgumentException  if the entity type is not spawnable
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @since 0.7.0
     */
    @Contract(value = "_, _ -> new", mutates = "this")
    EntityHologramLine setEntityPage(int index, EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException;

    /**
     * Inserts a text page at the specified index.
     *
     * @param index the index at which to insert the page
     * @return the newly created text page
     * @since 0.6.0
     */
    @Contract(value = "_ -> new", mutates = "this")
    TextHologramLine insertTextPage(int index) throws IndexOutOfBoundsException;

    /**
     * Inserts an item page at the specified index.
     *
     * @param index the index at which to insert the page
     * @return the newly created item page
     * @since 0.6.0
     */
    @Contract(value = "_ -> new", mutates = "this")
    ItemHologramLine insertItemPage(int index) throws IndexOutOfBoundsException;

    /**
     * Inserts a block page at the specified index.
     *
     * @param index the index at which to insert the page
     * @return the newly created block page
     * @since 0.6.0
     */
    @Contract(value = "_ -> new", mutates = "this")
    BlockHologramLine insertBlockPage(int index) throws IndexOutOfBoundsException;

    /**
     * Inserts an entity page at the specified index.
     *
     * @param index      the index at which to insert the page
     * @param entityType the type of entity to display
     * @return the newly created entity page
     * @throws IllegalArgumentException if the entity type is not spawnable
     * @since 0.6.0
     */
    @Contract(value = "_, _ -> new", mutates = "this")
    EntityHologramLine insertEntityPage(int index, EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException;

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
     * @throws IllegalArgumentException if the interval is not bigger than zero
     * @since 0.5.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    PagedHologramLine setInterval(Duration interval) throws IllegalArgumentException;

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

    /**
     * Cycles the page of this line for the given player.
     * <p>
     * This is equivalent to calling {@link #cyclePage(Player, int)} with an amount of one.
     *
     * @param player player
     * @return a future that completes when the page has been cycled
     * @see #cyclePage(Player, int)
     * @since 0.8.0
     */
    @Contract(mutates = "this")
    CompletableFuture<Boolean> cyclePage(Player player);

    /**
     * Cycles the page of this line for the given player by the given amount.
     * <p>
     * Opposed to {@link #setPage(Player, int)}, this method will not throw an exception if the page index is out of bounds and will instead wrap around.
     *
     * @param player player
     * @param amount amount
     * @return a future that completes when the page has been cycled
     * @apiNote Negative amounts will cycle backwards.
     * @since 0.8.0
     */
    @Contract(mutates = "this")
    CompletableFuture<Boolean> cyclePage(Player player, int amount);

    /**
     * Sets the page of this line for the given player.
     *
     * @param player player
     * @param page   page
     * @return a future that completes when the page has been set
     * @throws IndexOutOfBoundsException if the page index is out of bounds
     * @see #getPageCount()
     * @since 0.8.0
     */
    @Contract(mutates = "this")
    CompletableFuture<Boolean> setPage(Player player, int page) throws IndexOutOfBoundsException;

    /**
     * Gets the current page index of this line for the given player.
     *
     * @param player player
     * @return current page index
     * @since 0.8.0
     */
    @Contract(pure = true)
    OptionalInt getCurrentPageIndex(Player player);

    /**
     * Gets the current page of this line for the given player.
     *
     * @param player player
     * @return current page
     * @since 0.8.0
     */
    @Contract(pure = true)
    Optional<StaticHologramLine> getCurrentPage(Player player);

    /**
     * Iterates over all pages of this line.
     *
     * @param action action consumer
     * @since 0.8.0
     */
    void forEachPage(Consumer<StaticHologramLine> action);
}
