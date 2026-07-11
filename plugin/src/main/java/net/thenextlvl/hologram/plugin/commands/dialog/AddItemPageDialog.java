package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

@NullMarked
final class AddItemPageDialog {
    private AddItemPageDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final String initial,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var actions = new ArrayList<Button<?>>();
        actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
            final var created = line.addItemPage();
            created.setItemStack(ItemStack.of(Material.PLAYER_HEAD));
            created.setPlayerHead(true);
            DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
        }), Component.text("Player head", NamedTextColor.YELLOW)));
        final var held = DialogSupport.heldItemButton(viewer, "Use Held Item", (audience, item) -> {
            line.addItemPage().setItemStack(item);
            DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
        });
        if (held != null) actions.add(held);
        return ItemSearchDialog.create("Add Item Page", initial, note, actions, DialogSupport.editPagedBackButton(hologram, lineIndex, line),
                (audience, item) -> {
                    line.addItemPage().setItemStack(item);
                    DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
                });
    }
}
