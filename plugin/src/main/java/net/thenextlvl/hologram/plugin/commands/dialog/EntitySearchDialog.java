package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
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

    static DialogLike create(
            final String title,
            final String initial,
            @Nullable final Component note,
            final ActionButton back,
            final BiConsumer<Audience, EntityType> selection,
            final ActionButton... extraActions
    ) {
        return create(Component.text(title), initial, note, back, selection, extraActions);
    }

    static DialogLike create(
            final Component title,
            final String initial,
            @Nullable final Component note,
            final ActionButton back,
            final BiConsumer<Audience, EntityType> selection,
            final ActionButton... extraActions
    ) {
        return EntitySearchDialog.create(title, initial, note, back, selection, 0, extraActions);
    }

    static DialogLike create(
            final Component title,
            final String initial,
            @Nullable final Component note,
            final ActionButton back,
            final BiConsumer<Audience, EntityType> selection,
            final int page,
            final ActionButton... extraActions
    ) {
        final var query = DialogSupport.normalizeSearch(initial);
        final var matches = DialogSupport.searchEntities(query);
        final var pageCount = DialogSupport.pageCount(matches);
        final var currentPage = DialogSupport.clampPage(page, pageCount);
        final var search = ActionButton.builder(Component.text("Search", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = DialogSupport.input(response, "search");
                    final var result = DialogSupport.searchEntities(input);
                    final var message = result.isEmpty() ? Component.text("No matching entities found", NamedTextColor.RED) : null;
                    DialogSupport.show(audience, ignored -> EntitySearchDialog.create(title, input, message, back, selection, extraActions));
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var actions = new ArrayList<ActionButton>();
        if (currentPage > 0) actions.add(DialogSupport.pageButton("Previous Page", audience -> {
            DialogSupport.show(audience, ignored -> EntitySearchDialog.create(title, initial, note, back, selection, currentPage - 1, extraActions));
        }));
        if (currentPage + 1 < pageCount) actions.add(DialogSupport.pageButton("Next Page", audience -> {
            DialogSupport.show(audience, ignored -> EntitySearchDialog.create(title, initial, note, back, selection, currentPage + 1, extraActions));
        }));
        actions.add(search);
        actions.addAll(Arrays.asList(extraActions));
        matches.stream().skip((long) currentPage * DialogSupport.SEARCH_PAGE_SIZE).limit(DialogSupport.SEARCH_PAGE_SIZE).map(entity -> DialogSupport.selectionButton(entity, audience -> {
            selection.accept(audience, entity);
        })).forEach(actions::add);

        final var body = DialogSupport.searchBody("Search by friendly name or key", note, matches, currentPage, pageCount);
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(title)
                        .body(body)
                        .inputs(List.of(DialogInput.text("search", Component.text("Search")).initial(initial).build()))
                        .build())
                .type(DialogType.multiAction(actions).exitAction(back).build()));
    }
}
