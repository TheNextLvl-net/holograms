package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
final class VisualOptionsDialog {
    private VisualOptionsDialog() {
    }

    static DialogLike create(
            final String title,
            final Component header,
            @Nullable final Component note,
            final List<ActionButton> actions,
            final ActionButton back
    ) {
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(header));
        body.add(DialogBody.plainMessage(Component.text("Edit how this line looks")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(title))
                        .body(body)
                        .build())
                .type(DialogType.multiAction(DialogSupport.addBack(actions, back)).build()));
    }
}
