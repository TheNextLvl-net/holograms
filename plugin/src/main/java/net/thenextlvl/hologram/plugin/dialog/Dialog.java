package net.thenextlvl.hologram.plugin.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.thenextlvl.hologram.plugin.dialog.button.Button;
import net.thenextlvl.hologram.plugin.dialog.body.Body;
import net.thenextlvl.hologram.plugin.dialog.input.Input;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.List;

public interface Dialog<S extends Dialog<S>> {
    S title(Component title);

    S closeWithEscape(boolean close);

    S closeAction(DialogBase.DialogAfterAction afterAction);

    S externalTitle(@Nullable Component externalTitle);

    default S addBody(final Body body) {
        return addBody(body.build());
    }

    S addBody(DialogBody body);

    default S addInput(final Input<?> input) {
        return addInput(input.build());
    }

    S addInput(DialogInput input);

    static NoticeDialog notice(final Button<?> button) {
        return notice(button.build());
    }

    static NoticeDialog notice(final ActionButton button) {
        return null;
    }

    static NoticeDialog notice() {
        return null;
    }

    static NoticeDialog confirmation(final Button<?> confirm, final Button<?> cancel) {
        return confirmation(confirm.build(), cancel.build());
    }

    static NoticeDialog confirmation(final ActionButton confirm, final ActionButton cancel) {
        return null;
    }

    static void idk2() {
        io.papermc.paper.dialog.Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("title"))
                        .canCloseWithEscape(true)
                        .afterAction(DialogBase.DialogAfterAction.NONE)
                        .body(List.of(
                                DialogBody.plainMessage(Component.text("idk"), 10),
                                DialogBody.item(ItemStack.of(Material.STONE))
                                        .description(DialogBody.plainMessage(Component.text("idk")))
                                        .height(5)
                                        .showDecorations(true)
                                        .showTooltip(false)
                                        .width(22)
                                        .build()
                        ))
                        .externalTitle(Component.text("This button opens SOMETHING"))
                        .inputs(List.of(
                                DialogInput.bool("test", Component.text("Test"))
                                        .initial(true)
                                        .onFalse("False")
                                        .onTrue("True")
                                        .build(),
                                DialogInput.singleOption("option", Component.text("Option"), List.of(
                                        SingleOptionDialogInput.OptionEntry.create("idk", Component.text("Idk"), true),
                                        SingleOptionDialogInput.OptionEntry.create("idk2", Component.text("Idk2"), false)
                                )).labelVisible(true).width(100).build(),
                                DialogInput.text("idk", Component.text("idk"))
                                        .initial("whut")
                                        .labelVisible(true)
                                        .width(100)
                                        .maxLength(100)
                                        .multiline(TextDialogInput.MultilineOptions.create(2, 5))
                                        .build(),
                                DialogInput.numberRange("number", Component.text("number"), 100, 200)
                                        .initial(123f)
                                        .labelFormat("%s: %s")
                                        .step(2f)
                                        .width(2)
                                        .build()
                        ))
                        .build())
                .type(DialogType.confirmation(null, null))
                .type(DialogType.multiAction(List.of())
                        .exitAction(null)
                        .columns(5)
                        .build())
                .type(DialogType.notice(ActionButton.builder(Component.text("button"))

                        .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                        })))

                        .action(DialogAction.customClick((response, audience) -> {
                        }, ClickCallback.Options.builder()
                                .lifetime(ClickCallback.DEFAULT_LIFETIME)
                                .uses(ClickCallback.UNLIMITED_USES)
                                .build()))

                        .action(DialogAction.commandTemplate("hello"))

                        .tooltip(Component.text("tooltip"))
                        .width(200)
                        .build())));
    }

    static Dialog<?> idk() {
        Button.clickEvent(ClickEvent.callback(audience -> {
                }), Component.text("button"))
                .tooltip(Component.text("tooltip"))
                .width(200);
        Button.customClick((response, audience) -> {
                }, Component.text("button"))
                .lifetime(Duration.ofSeconds(22))
                .uses(1)
                .tooltip(Component.text("tooltip"));
        Button.commandTemplate("say $(input)", Component.text("template"))
                .width(22);
        Button.dummy(Component.text("This button does nothing :)"));

        Dialog.confirmation((ActionButton) null, null);
        Dialog.multiAction()
                .exitAction(null)
                .addButton(null)
                .columns(5);

        return Dialog.notice()
                .title(Component.text("title"))
                .externalTitle(Component.text("This button opens SOMETHING"))
                .closeWithEscape(true)
                .closeAction(DialogBase.DialogAfterAction.NONE)
                .addBody(Body.text(Component.text("idk"), 10))
                .addBody(Body.item(Material.STONE)
                        .description(Component.text("idk"))
                        .height(5)
                        .showDecorations(true)
                        .showTooltip(false)
                        .width(22))
                .addInput(Input.bool("test", Component.text("Test"))
                        .initial(true)
                        .onFalse("False")
                        .onTrue("True"))
                .addInput(Input.option("option", Component.text("Option"))
                        .addOption("idk", Component.text("Idk"))
                        .addOption("idk2", Component.text("Idk2"))
                        .initial("idk2")
                        .hideLabel(false)
                        .width(100))
                .addInput(Input.text("idk", Component.text("idk"))
                        .initial("whut")
                        .hideLabel(false)
                        .width(100)
                        .maxLength(100)
                        .maxInputLines(2)
                        .inputHeight(5))
                .addInput(Input.slider("number", Component.text("number"), 100, 200)
                        .initial(123f)
                        .labelFormat("%s: %s")
                        .step(2f)
                        .width(2));
    }

    static MultiActionDialog multiAction() {
        return null;
    }
}
