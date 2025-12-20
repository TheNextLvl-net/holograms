package net.thenextlvl.hologram;

import io.papermc.paper.math.Position;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.key.Key;
import net.thenextlvl.hologram.adapters.PositionAdapter;
import net.thenextlvl.hologram.commands.HologramCommand;
import net.thenextlvl.hologram.controller.PaperHologramController;
import net.thenextlvl.hologram.listeners.ChunkListener;
import net.thenextlvl.hologram.listeners.HologramListener;
import net.thenextlvl.i18n.ComponentBundle;
import net.thenextlvl.nbt.serialization.NBT;
import org.bstats.bukkit.Metrics;
import org.bukkit.World;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

@NullMarked
public class HologramPlugin extends JavaPlugin {
    public static final String ISSUES = "https://github.com/TheNextLvl-net/holograms/issues/new";

    private final PaperHologramController controller = new PaperHologramController(this);
    private final Metrics metrics = new Metrics(this, 25817);

    private final Key key = Key.key("characters", "translations");
    private final Path translations = getDataPath().resolve("translations");
    private final ComponentBundle bundle = ComponentBundle.builder(key, translations)
            .placeholder("prefix", "prefix")
            .resource("messages.properties", Locale.US)
            .resource("messages_german.properties", Locale.GERMANY)
            .build();

    public HologramPlugin() {
        getServer().getServicesManager().register(HologramController.class, controller, this, ServicePriority.Normal);
    }

    @Override
    public void onEnable() {
        registerCommands();
        registerListeners();
    }

    private void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(HologramCommand.create(this), "The main command to interact with holograms", Set.of("holo"));
        });
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ChunkListener(this), this);
        getServer().getPluginManager().registerEvents(new HologramListener(this), this);
    }

    @Override
    public void onDisable() {
        controller.forEachHologram(Hologram::persist);
        metrics.shutdown();
    }

    public PaperHologramController hologramController() {
        return controller;
    }

    public ComponentBundle bundle() {
        return bundle;
    }

    @Contract(value = "_ -> new", pure = true)
    public NBT nbt(World world) {
        return NBT.builder()
                .registerTypeHierarchyAdapter(Position.class, new PositionAdapter())
                // .registerTypeHierarchyAdapter(Key.class, new KeyAdapter())
                .build();
    }
}
