package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.line.HologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@NullMarked
final class EditActionCostDialog {
    private EditActionCostDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<?> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, DialogLike> reopen
    ) {
        final var currentCost = Double.toString(action.getCost());
        final var currentCurrency = action.getCurrency().orElse("");
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var cost = DialogSupport.parseDouble("Cost", response.getText("cost"), 0, Double.MAX_VALUE);
                    if (cost.error() != null) {
                        DialogSupport.show(audience, ignored -> EditActionCostDialog.create(hologram, line, actionName, action, header, Component.text(cost.error(), NamedTextColor.RED), reopen));
                        return;
                    }

                    final var currency = response.getText("currency");
                    action.setCost(cost.value());
                    action.setCurrency(currency != null && currency.isBlank() ? null : currency);
                    DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen));
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen)))))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Set the action cost and optional currency")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Cost"))
                        .body(body)
                        .inputs(List.of(
                                DialogInput.text("cost", Component.text("Cost")).initial(currentCost).build(),
                                DialogInput.text("currency", Component.text("Currency")).initial(currentCurrency).build()
                        ))
                        .build())
                .type(DialogType.multiAction(List.of(save)).exitAction(back).build()));
    }
}
