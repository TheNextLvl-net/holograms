package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class SimpleDialogBuilder implements DialogBuilder {
    private final Component title;
    private final List<DialogBody> body = new ArrayList<>();
    private final List<ActionButton> actionButtons = new ArrayList<>();

    public SimpleDialogBuilder(final Component title) {
        this.title = title;
    }

    @Override
    public DialogBuilder addBody(final DialogBody body) {
        this.body.add(body);
        return this;
    }

    @Override
    public DialogBuilder addBody(final Component text) {
        return addBody(DialogBody.plainMessage(text));
    }

    @Override
    public DialogBuilder body(final List<DialogBody> body) {
        this.body.clear();
        this.body.addAll(body);
        return this;
    }

    @Override
    public DialogLike build() {
        return Dialog.create(builder1 -> builder1.empty()
                .base(DialogBase.builder(title)
                        .body(body)
                        .build())
                .type(DialogType.multiAction(actions).build()));
    }
}
