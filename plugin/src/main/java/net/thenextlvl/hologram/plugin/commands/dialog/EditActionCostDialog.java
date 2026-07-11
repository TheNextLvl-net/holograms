package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
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

    static net.thenextlvl.dialogs.Dialog<?> create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<?> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, net.thenextlvl.dialogs.Dialog<?>> reopen
    ) {
        final var currentCost = Double.toString(action.getCost());
        final var currentCurrency = action.getCurrency().orElse("");
        final var save = Button.callback((response, audience) -> {
            final var cost = DialogSupport.parseDouble("Cost", response.getText("cost"), 0, Double.MAX_VALUE);
            if (cost.error() != null) {
                DialogSupport.show(audience, ignored -> EditActionCostDialog.create(hologram, line, actionName, action, header, Component.text(cost.error(), NamedTextColor.RED), reopen));
                return;
            }

            final var currency = response.getText("currency");
            action.setCost(cost.value());
            action.setCurrency(currency != null && currency.isBlank() ? null : currency);
            DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen));
        }, Component.text("Save", NamedTextColor.GREEN)).uses(1);
        final var back = BackButton.create(ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen));
        final var body = new ArrayList<DialogBody>();
        body.add(Body.text(Component.text("Set the action cost and optional currency")));
        if (note != null) body.add(Body.text(note));
        final var dialog = Dialog.multiAction().title(Component.text("Cost"));
        body.forEach(dialog::addBody);
        List.of(
                Input.text("cost", Component.text("Cost")).initial(currentCost).build(),
                Input.text("currency", Component.text("Currency")).initial(currentCurrency).build()
        ).forEach(dialog::addInput);
        List.of(save).forEach(dialog::addButton);
        dialog.exitAction(back);
        return dialog;
    }
}
