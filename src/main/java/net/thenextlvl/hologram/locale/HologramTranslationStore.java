package net.thenextlvl.hologram.locale;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.locale.store.MutableMiniMessageTranslationStore;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class HologramTranslationStore extends MutableMiniMessageTranslationStore {
    private final HologramPlugin plugin;

    public HologramTranslationStore(HologramPlugin plugin) {
        super(Key.key("holograms", "translations"), MiniMessage.miniMessage());
        this.plugin = plugin;
    }

    public void read() {
    }

    public void save() {
    }

    @Override
    protected void onUpdate() {
        plugin.updateHologramTextLines(null);
    }
}
