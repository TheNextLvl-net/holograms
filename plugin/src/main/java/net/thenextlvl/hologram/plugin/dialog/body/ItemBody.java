package net.thenextlvl.hologram.plugin.dialog.body;

import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Range;

public interface ItemBody extends Body {
    ItemBody description(PlainMessageDialogBody description);

    default ItemBody description(final Component text) {
        return description(Body.text(text));
    }

    default ItemBody description(final Component text, @Range(from = 1, to = 1024) final int width) {
        return description(Body.text(text, width));
    }

    ItemBody height(@Range(from = 1, to = 256) int height);

    ItemBody showDecorations(boolean decorations);

    ItemBody showTooltip(boolean tooltip);

    ItemBody width(@Range(from = 1, to = 256) int width);
}
