package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@NullMarked
final class ItemSearchDialog {
    private ItemSearchDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final String title,
            final String initial,
            @Nullable final Component note,
            final List<Button<?>> extraActions,
            final Button<?> back,
            final BiConsumer<Audience, ItemStack> selection
    ) {
        return create(Component.text(title), initial, note, extraActions, back, selection);
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final Component title,
            final String initial,
            @Nullable final Component note,
            final List<Button<?>> extraActions,
            final Button<?> back,
            final BiConsumer<Audience, ItemStack> selection
    ) {
        return ItemSearchDialog.create(title, initial, note, extraActions, back, selection, 0);
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final Component title,
            final String initial,
            @Nullable final Component note,
            final List<Button<?>> extraActions,
            final Button<?> back,
            final BiConsumer<Audience, ItemStack> selection,
            final int page
    ) {
        final var query = DialogSupport.normalizeSearch(initial);
        final var matches = DialogSupport.searchMaterials(query, Material::isItem);
        final var pageCount = DialogSupport.pageCount(matches);
        final var currentPage = DialogSupport.clampPage(page, pageCount);
        final var search = Button.callback((response, audience) -> {
            final var input = DialogSupport.input(response, "search");
            final var result = DialogSupport.searchMaterials(input, Material::isItem);
            final var message = result.isEmpty() ? Component.text("No matching items found", NamedTextColor.RED) : null;
            DialogSupport.show(audience, ignored -> ItemSearchDialog.create(title, input, message, extraActions, back, selection));
        }, Component.text("Search", NamedTextColor.GREEN)).uses(1);
        final var actions = new ArrayList<Button<?>>();
        if (currentPage > 0) actions.add(DialogSupport.pageButton("Previous Page", audience -> {
            return ItemSearchDialog.create(title, initial, note, extraActions, back, selection, currentPage - 1);
        }));
        if (currentPage + 1 < pageCount) actions.add(DialogSupport.pageButton("Next Page", audience -> {
            return ItemSearchDialog.create(title, initial, note, extraActions, back, selection, currentPage + 1);
        }));
        actions.add(search);
        actions.addAll(extraActions);
        matches.stream().skip((long) currentPage * DialogSupport.SEARCH_PAGE_SIZE).limit(DialogSupport.SEARCH_PAGE_SIZE).map(material -> DialogSupport.selectionButton(material, audience -> {
            selection.accept(audience, ItemStack.of(material));
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
