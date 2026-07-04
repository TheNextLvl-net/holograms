package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

@NullMarked
final class AddItemLineDialog {
    private AddItemLineDialog() {
    }

    static DialogLike create(final Hologram hologram, final String initial, @Nullable final Component note, final Audience viewer) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> AddLineTypeDialog.create(hologram));
                }))).build();
        final var actions = new ArrayList<ActionButton>();
        actions.add(ActionButton.builder(Component.text("Player head", NamedTextColor.YELLOW))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    final var line = hologram.addItemLine();
                    line.setItemStack(ItemStack.of(Material.PLAYER_HEAD));
                    line.setPlayerHead(true);
                    DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                }))).build());
        final var held = DialogSupport.heldItemButton(viewer, "Use Held Item", (audience, item) -> {
            hologram.addItemLine().setItemStack(item);
            DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
        });
        if (held != null) actions.add(held);
        return ItemSearchDialog.create("Add Item Line", initial, note, actions, back, (audience, item) -> {
            hologram.addItemLine().setItemStack(item);
            DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
        });
    }
}
