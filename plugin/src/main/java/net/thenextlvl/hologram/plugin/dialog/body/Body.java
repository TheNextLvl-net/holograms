package net.thenextlvl.hologram.plugin.dialog.body;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Range;

public interface Body {
    static ItemBody item(final Material material) {
        return item(ItemStack.of(material));
    }

    static ItemBody item(final ItemStack itemStack) {
        return null;
    }

    static PlainMessageDialogBody text(final Component text) {
        return DialogBody.plainMessage(text);
    }

    static PlainMessageDialogBody text(final Component text, @Range(from = 1, to = 1024) final int width) {
        return DialogBody.plainMessage(text, width);
    }

    DialogBody build();
}
