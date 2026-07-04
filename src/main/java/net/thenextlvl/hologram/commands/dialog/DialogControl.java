package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@NullMarked
final class DialogControl implements DialogLike {
    private final Component title;
    private final List<DialogBody> body = new ArrayList<>();
    private final List<DialogInput> inputs = new ArrayList<>();
    private final List<ActionButton> actions = new ArrayList<>();
    private int columns;
    private @Nullable ActionButton exitAction;

    private DialogControl(final Component title) {
        this.title = title;
    }

    static DialogControl create(final String title) {
        return create(Component.text(title));
    }

    static DialogControl create(final Component title) {
        return new DialogControl(title);
    }

    DialogControl body(final Component line) {
        body.add(DialogBody.plainMessage(line));
        return this;
    }

    DialogControl body(final String line) {
        return body(Component.text(line));
    }

    DialogControl note(@Nullable final Component note) {
        if (note != null) body(note);
        return this;
    }

    DialogControl input(final DialogInput input) {
        inputs.add(input);
        return this;
    }

    DialogControl action(final ActionButton action) {
        actions.add(action);
        return this;
    }

    DialogControl actions(final Iterable<ActionButton> actions) {
        actions.forEach(this.actions::add);
        return this;
    }

    DialogControl button(final Component label, final Function<Audience, DialogLike> dialog) {
        return action(DialogControl.actionButton(label, dialog));
    }

    DialogControl button(final Component label, final int width, final Function<Audience, DialogLike> dialog) {
        return action(DialogControl.actionButton(label, width, dialog));
    }

    DialogControl directButton(final Component label, final int width, final Function<Audience, DialogLike> dialog) {
        return action(ActionButton.builder(label).action(openDirect(dialog)).width(width).build());
    }

    DialogControl submit(final Component label, final DialogActionCallback handler) {
        return action(ActionButton.builder(label).action(submit(handler)).build());
    }

    DialogControl submit(final Component label, final int width, final DialogActionCallback handler) {
        return action(ActionButton.builder(label).action(submit(handler)).width(width).build());
    }

    DialogControl back(final Function<Audience, DialogLike> dialog) {
        exitAction = DialogControl.backButton(dialog);
        return this;
    }

    DialogControl back(final int width, final Function<Audience, DialogLike> dialog) {
        exitAction = DialogControl.backButton(width, dialog);
        return this;
    }

    DialogControl exit(final ActionButton action) {
        exitAction = action;
        return this;
    }

    DialogControl columns(final int columns) {
        this.columns = columns;
        return this;
    }

    DialogLike build() {
        return Dialog.create(builder -> {
            final var base = DialogBase.builder(title)
                    .body(body)
                    .inputs(inputs)
                    .build();
            final var type = DialogType.multiAction(actions);
            if (columns > 0) type.columns(columns);
            if (exitAction != null) type.exitAction(exitAction);
            builder.empty().base(base).type(type.build());
        });
    }

    static void show(
            final Audience audience,
            final Map<UUID, Function<Audience, DialogLike>> history,
            final Function<Audience, DialogLike> dialog
    ) {
        if (audience instanceof final Player player) history.put(player.getUniqueId(), dialog);
        audience.showDialog(resolve(dialog.apply(audience)));
    }

    static DialogAction open(final Function<Audience, DialogLike> dialog) {
        return DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, dialog)));
    }

    static DialogAction openDirect(final Function<Audience, DialogLike> dialog) {
        return DialogAction.staticAction(ClickEvent.callback(audience -> audience.showDialog(resolve(dialog.apply(audience)))));
    }

    static DialogAction submit(final DialogActionCallback handler) {
        return DialogAction.customClick(handler, ClickCallback.Options.builder().uses(1).build());
    }

    static ActionButton actionButton(final Component label, final Function<Audience, DialogLike> dialog) {
        return ActionButton.builder(label).action(open(dialog)).build();
    }

    static ActionButton actionButton(final Component label, final int width, final Function<Audience, DialogLike> dialog) {
        return ActionButton.builder(label).action(open(dialog)).width(width).build();
    }

    static ActionButton backButton(final Function<Audience, DialogLike> dialog) {
        return actionButton(Component.text("Back"), dialog);
    }

    static ActionButton backButton(final int width, final Function<Audience, DialogLike> dialog) {
        return actionButton(Component.text("Back"), width, dialog);
    }

    static ActionButton page(final String label, final java.util.function.Consumer<Audience> action) {
        return ActionButton.builder(Component.text(label, NamedTextColor.YELLOW))
                .action(DialogAction.staticAction(ClickEvent.callback(action::accept)))
                .build();
    }

    static DialogLike resolve(final DialogLike dialog) {
        return dialog instanceof final DialogControl control ? control.build() : dialog;
    }
}
