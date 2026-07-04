package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;

import java.util.List;

public interface DialogBuilder {
    static DialogBuilder builder(final Component title) {
        return new SimpleDialogBuilder(title);
    }

    DialogBuilder addBody(DialogBody body);

    DialogBuilder addBody(Component text);

    DialogBuilder body(List<DialogBody> body);

    DialogLike build();
}
