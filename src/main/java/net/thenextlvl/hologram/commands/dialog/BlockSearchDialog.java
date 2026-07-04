package net.thenextlvl.hologram.commands.dialog;

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
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@NullMarked
final class BlockSearchDialog {
    private BlockSearchDialog() {
    }

    static DialogLike create(
            final String title,
            final String initial,
            @Nullable final Component note,
            final List<ActionButton> extraActions,
            final ActionButton back,
            final BiConsumer<Audience, BlockData> selection
    ) {
        return BlockSearchDialog.create(Component.text(title), initial, note, extraActions, back, selection);
    }

    static DialogLike create(
            final Component title,
            final String initial,
            @Nullable final Component note,
            final List<ActionButton> extraActions,
            final ActionButton back,
            final BiConsumer<Audience, BlockData> selection
    ) {
        return BlockSearchDialog.create(title, initial, note, extraActions, back, selection, 0);
    }

    static DialogLike create(
            final Component title,
            final String initial,
            @Nullable final Component note,
            final List<ActionButton> extraActions,
            final ActionButton back,
            final BiConsumer<Audience, BlockData> selection,
            final int page
    ) {
        final var query = DialogSupport.normalizeSearch(initial);
        final var matches = DialogSupport.searchMaterials(query, Material::isBlock);
        final var pageCount = DialogSupport.pageCount(matches);
        final var currentPage = DialogSupport.clampPage(page, pageCount);
        final var search = ActionButton.builder(Component.text("Search", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = DialogSupport.input(response, "search");
                    final var result = DialogSupport.searchMaterials(input, Material::isBlock);
                    final var message = result.isEmpty() ? Component.text("No matching blocks found", NamedTextColor.RED) : null;
                    DialogSupport.show(audience, ignored -> BlockSearchDialog.create(title, input, message, extraActions, back, selection));
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var actions = new ArrayList<ActionButton>();
        if (currentPage > 0) actions.add(DialogSupport.pageButton("Previous Page", audience -> {
            DialogSupport.show(audience, ignored -> BlockSearchDialog.create(title, initial, note, extraActions, back, selection, currentPage - 1));
        }));
        if (currentPage + 1 < pageCount) actions.add(DialogSupport.pageButton("Next Page", audience -> {
            DialogSupport.show(audience, ignored -> BlockSearchDialog.create(title, initial, note, extraActions, back, selection, currentPage + 1));
        }));
        actions.add(search);
        actions.addAll(extraActions);
        matches.stream().skip((long) currentPage * DialogSupport.SEARCH_PAGE_SIZE).limit(DialogSupport.SEARCH_PAGE_SIZE).map(material -> DialogSupport.selectionButton(material, audience -> {
            selection.accept(audience, material.createBlockData());
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
