package net.thenextlvl.hologram.plugin.dialog.button;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.Range;

public interface Button<T extends Button<T>> {
    static ClickEventButton clickEvent(final ClickEvent<?> callback, final Component title) {
        return null;
    }

    static CustomClickButton customClick(final DialogActionCallback callback, final Component title) {
        return null;
    }

    static CommandTemplateButton commandTemplate(final String template, final Component title) {
        return null;
    }

    static DummyButton dummy(final Component text) {
        return null;
    }

    T tooltip(Component tooltip);

    T width(@Range(from = 1, to = 1024) int width);

    ActionButton build();
}
