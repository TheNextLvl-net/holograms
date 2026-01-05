package net.thenextlvl.hologram;

import dev.faststats.bukkit.BukkitMetrics;
import io.papermc.paper.math.Position;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.thenextlvl.binder.StaticBinder;
import net.thenextlvl.hologram.adapters.BlockDataAdapter;
import net.thenextlvl.hologram.adapters.BrightnessAdapter;
import net.thenextlvl.hologram.adapters.ColorAdapter;
import net.thenextlvl.hologram.adapters.ComponentAdapter;
import net.thenextlvl.hologram.adapters.ItemStackAdapter;
import net.thenextlvl.hologram.adapters.PositionAdapter;
import net.thenextlvl.hologram.adapters.QuaternionfAdapter;
import net.thenextlvl.hologram.adapters.TransformationAdapter;
import net.thenextlvl.hologram.adapters.Vector3fAdapter;
import net.thenextlvl.hologram.adapters.serializers.BlockHologramLineSerializer;
import net.thenextlvl.hologram.adapters.serializers.EntityHologramLineSerializer;
import net.thenextlvl.hologram.adapters.serializers.ItemHologramLineSerializer;
import net.thenextlvl.hologram.adapters.serializers.TextHologramLineSerializer;
import net.thenextlvl.hologram.commands.HologramCommand;
import net.thenextlvl.hologram.controller.PaperHologramProvider;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.hologram.listeners.ChunkListener;
import net.thenextlvl.hologram.listeners.HologramListener;
import net.thenextlvl.i18n.ComponentBundle;
import net.thenextlvl.nbt.serialization.NBT;
import net.thenextlvl.nbt.serialization.adapters.EnumAdapter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Display;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

@NullMarked
public class HologramPlugin extends JavaPlugin {
    public static final String ISSUES = "https://github.com/TheNextLvl-net/holograms/issues/new";

    private final PaperHologramProvider provider = new PaperHologramProvider(this);
    private final Metrics metrics = new Metrics(this, 25817);
    private final dev.faststats.core.Metrics fastStats = BukkitMetrics.factory()
            .token("27b63937a461e94208f25b105af290cf")
            .create(this);

    private final Key key = Key.key("characters", "translations");
    private final Path translations = getDataPath().resolve("translations");
    private final ComponentBundle bundle = ComponentBundle.builder(key, translations)
            .placeholder("prefix", "prefix")
            .resource("messages.properties", Locale.US)
            .resource("messages_german.properties", Locale.GERMANY)
            .build();

    public HologramPlugin() {
        StaticBinder.getInstance(HologramProvider.class.getClassLoader()).bind(HologramProvider.class, provider);
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
        provider.forEachHologram(Hologram::persist);
        metrics.shutdown();
    }

    public PaperHologramProvider hologramProvider() {
        return provider;
    }

    public ComponentBundle bundle() {
        return bundle;
    }

    private NBT.Builder base(World world) {
        return NBT.builder()
                .registerTypeHierarchyAdapter(BlockData.class, new BlockDataAdapter(getServer()))
                .registerTypeHierarchyAdapter(Color.class, new ColorAdapter())
                .registerTypeHierarchyAdapter(Component.class, new ComponentAdapter())
                .registerTypeHierarchyAdapter(Display.Billboard.class, new EnumAdapter<>(Display.Billboard.class))
                .registerTypeHierarchyAdapter(Display.Brightness.class, new BrightnessAdapter())
                .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
                .registerTypeHierarchyAdapter(LineType.class, new EnumAdapter<>(LineType.class))
                .registerTypeHierarchyAdapter(Position.class, new PositionAdapter())
                .registerTypeHierarchyAdapter(Quaternionf.class, new QuaternionfAdapter())
                .registerTypeHierarchyAdapter(Transformation.class, new TransformationAdapter())
                .registerTypeHierarchyAdapter(Vector3f.class, new Vector3fAdapter())
                // .registerTypeHierarchyAdapter(Key.class, new KeyAdapter())
                ;
    }

    public NBT serializer(World world) {
        return base(world)
                .registerTypeHierarchyAdapter(BlockHologramLine.class, new BlockHologramLineSerializer())
                .registerTypeHierarchyAdapter(EntityHologramLine.class, new EntityHologramLineSerializer())
                .registerTypeHierarchyAdapter(ItemHologramLine.class, new ItemHologramLineSerializer())
                .registerTypeHierarchyAdapter(TextHologramLine.class, new TextHologramLineSerializer())
                .build();
    }

    public NBT deserializer(World world) {
        return base(world)
                .registerTypeHierarchyAdapter(EntityHologramLine.class, new EntityHologramLineSerializer())
                .build();
    }
}
