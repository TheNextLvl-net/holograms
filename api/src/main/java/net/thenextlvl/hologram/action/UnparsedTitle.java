package net.thenextlvl.hologram.action;

import net.kyori.adventure.title.Title;
import org.jspecify.annotations.Nullable;

/**
 * Represents an unparsed title.
 *
 * @param title    The title.
 * @param subtitle The subtitle.
 * @param times    The times.
 * @since 0.8.0
 */
public record UnparsedTitle(String title, String subtitle, Title.@Nullable Times times) {
}
