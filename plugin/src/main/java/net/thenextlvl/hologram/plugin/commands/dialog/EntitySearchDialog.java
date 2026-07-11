package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

@NullMarked
final class EntitySearchDialog {
    private EntitySearchDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final String title,
            final String initial,
            @Nullable final Component note,
            final Button<?> back,
            final BiConsumer<Audience, EntityType> selection,
            final Button<?>... extraActions
    ) {
        return create(Component.text(title), initial, note, back, selection, extraActions);
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final Component title,
            final String initial,
            @Nullable final Component note,
            final Button<?> back,
            final BiConsumer<Audience, EntityType> selection,
            final Button<?>... extraActions
    ) {
        return EntitySearchDialog.create(title, initial, note, back, selection, 0, extraActions);
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final Component title,
            final String initial,
            @Nullable final Component note,
            final Button<?> back,
            final BiConsumer<Audience, EntityType> selection,
            final int page,
            final Button<?>... extraActions
    ) {
        final var query = DialogSupport.normalizeSearch(initial);
        final var matches = DialogSupport.searchEntities(query);
        final var pageCount = DialogSupport.pageCount(matches);
        final var currentPage = DialogSupport.clampPage(page, pageCount);
        final var search = Button.callback((response, audience) -> {
            final var input = DialogSupport.input(response, "search");
            final var result = DialogSupport.searchEntities(input);
            final var message = result.isEmpty() ? Component.text("No matching entities found", NamedTextColor.RED) : null;
            DialogSupport.show(audience, ignored -> EntitySearchDialog.create(title, input, message, back, selection, extraActions));
        }, Component.text("Search", NamedTextColor.GREEN)).uses(1);
        final var actions = new ArrayList<Button<?>>();
        if (currentPage > 0) actions.add(DialogSupport.pageButton("Previous Page", audience -> {
            return EntitySearchDialog.create(title, initial, note, back, selection, currentPage - 1, extraActions);
        }));
        if (currentPage + 1 < pageCount) actions.add(DialogSupport.pageButton("Next Page", audience -> {
            return EntitySearchDialog.create(title, initial, note, back, selection, currentPage + 1, extraActions);
        }));
        actions.add(search);
        actions.addAll(Arrays.asList(extraActions));
        matches.stream().skip((long) currentPage * DialogSupport.SEARCH_PAGE_SIZE).limit(DialogSupport.SEARCH_PAGE_SIZE).map(entity -> DialogSupport.selectionButton(entity, audience -> {
            selection.accept(audience, entity);
        })).forEach(actions::add);

        final var body = DialogSupport.searchBody("Search by friendly name or key", note, matches, currentPage, pageCount);
        final var dialog = Dialog.multiAction().title(title);
        body.forEach(dialog::addBody);
        List.of(Input.text("search", Component.text("Search")).initial(initial).build()).forEach(dialog::addInput);
        actions.forEach(dialog::addButton);
        dialog.exitAction(back);
        return dialog;
    }
}
