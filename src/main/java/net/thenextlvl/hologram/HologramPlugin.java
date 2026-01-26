package net.thenextlvl.hologram;

import dev.faststats.bukkit.BukkitMetrics;
import dev.faststats.core.ErrorTracker;
import io.papermc.paper.ServerBuildInfo;
import io.papermc.paper.math.Position;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.binder.StaticBinder;
import net.thenextlvl.hologram.adapters.BlockDataAdapter;
import net.thenextlvl.hologram.adapters.BrightnessAdapter;
import net.thenextlvl.hologram.adapters.ColorAdapter;
import net.thenextlvl.hologram.adapters.ComponentAdapter;
import net.thenextlvl.hologram.adapters.ItemStackAdapter;
import net.thenextlvl.hologram.adapters.Matrix4fAdapter;
import net.thenextlvl.hologram.adapters.PositionAdapter;
import net.thenextlvl.hologram.adapters.QuaternionfAdapter;
import net.thenextlvl.hologram.adapters.TextColorAdapter;
import net.thenextlvl.hologram.adapters.TransformationAdapter;
import net.thenextlvl.hologram.adapters.Vector3fAdapter;
import net.thenextlvl.hologram.adapters.deserializers.BlockHologramLineDeserializer;
import net.thenextlvl.hologram.adapters.deserializers.EntityHologramLineDeserializer;
import net.thenextlvl.hologram.adapters.deserializers.ItemHologramLineDeserializer;
import net.thenextlvl.hologram.adapters.deserializers.PagedHologramLineDeserializer;
import net.thenextlvl.hologram.adapters.deserializers.TextHologramLineDeserializer;
import net.thenextlvl.hologram.adapters.serializers.BlockHologramLineSerializer;
import net.thenextlvl.hologram.adapters.serializers.EntityHologramLineSerializer;
import net.thenextlvl.hologram.adapters.serializers.ItemHologramLineSerializer;
import net.thenextlvl.hologram.adapters.serializers.PagedHologramLineSerializer;
import net.thenextlvl.hologram.adapters.serializers.TextHologramLineSerializer;
import net.thenextlvl.hologram.commands.HologramCommand;
import net.thenextlvl.hologram.controller.PaperHologramProvider;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.hologram.listeners.ChunkListener;
import net.thenextlvl.hologram.listeners.EntityListener;
import net.thenextlvl.hologram.listeners.HologramListener;
import net.thenextlvl.hologram.listeners.LocaleListener;
import net.thenextlvl.hologram.listeners.PluginListener;
import net.thenextlvl.hologram.listeners.WorldListener;
import net.thenextlvl.hologram.locale.HologramTranslationStore;
import net.thenextlvl.hologram.locale.MiniPlaceholdersFormatter;
import net.thenextlvl.hologram.locale.PlaceholderAPIFormatter;
import net.thenextlvl.hologram.models.PaperHologram;
import net.thenextlvl.hologram.models.line.PaperBlockHologramLine;
import net.thenextlvl.hologram.models.line.PaperEntityHologramLine;
import net.thenextlvl.hologram.models.line.PaperItemHologramLine;
import net.thenextlvl.hologram.models.line.PaperPagedHologramLine;
import net.thenextlvl.hologram.models.line.PaperTextHologramLine;
import net.thenextlvl.hologram.version.PluginVersionChecker;
import net.thenextlvl.i18n.ComponentBundle;
import net.thenextlvl.nbt.NBTInputStream;
import net.thenextlvl.nbt.serialization.NBT;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.adapters.EnumAdapter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

@NullMarked
public class HologramPlugin extends JavaPlugin {
    public static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware();
    public static final String ISSUES = "https://github.com/TheNextLvl-net/holograms/issues/new?template=bug_report.yml";
    public static final boolean RUNNING_FOLIA = ServerBuildInfo.buildInfo().isBrandCompatible(Key.key("papermc", "folia"));

    private final PaperHologramProvider provider = new PaperHologramProvider(this);
    private final Metrics metrics = new Metrics(this, 25817);
    private final dev.faststats.core.Metrics fastStats = BukkitMetrics.factory()
            .token("27b63937a461e94208f25b105af290cf")
            .errorTracker(ERROR_TRACKER)
            .create(this);

    private final PluginVersionChecker versionChecker = new PluginVersionChecker(this);

    private final Key key = Key.key("holograms", "translations");
    private final Path path = getDataPath().resolve("translations");
    private final ComponentBundle bundle = ComponentBundle.builder(key, path)
            .placeholder("prefix", "prefix")
            .resource("messages.properties", Locale.US)
            .resource("messages_german.properties", Locale.GERMANY)
            .build();

    private final HologramTranslationStore translations = new HologramTranslationStore(this);

    public @Nullable MiniPlaceholdersFormatter miniFormatter = null;
    public @Nullable PlaceholderAPIFormatter papiFormatter = null;

    public HologramPlugin() {
        StaticBinder.getInstance(HologramProvider.class.getClassLoader()).bind(HologramProvider.class, provider);
    }

    @Override
    public void onLoad() {
        versionChecker.checkVersion();
    }

    @Override
    public void onEnable() {
        getServer().getGlobalRegionScheduler().runAtFixedRate(this, ignored -> {
            hologramProvider().getHolograms().map(PaperHologram.class::cast).forEach(hologram -> { // fixme: fix cast
                getServer().getOnlinePlayers().forEach(hologram::updateVisibility);
            });
        }, 100L, 100L);
        registerCommands();
        registerListeners();
    }

    private void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> {
            event.registrar().register(HologramCommand.create(this), "The main command to interact with holograms", Set.of("holo"));
        }));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ChunkListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new HologramListener(this), this);
        getServer().getPluginManager().registerEvents(new LocaleListener(this), this);
        getServer().getPluginManager().registerEvents(new PluginListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
    }

    @Override
    public void onDisable() {
        provider.forEachHologram(Hologram::persist);
        fastStats.shutdown();
        metrics.shutdown();
    }

    public PaperHologramProvider hologramProvider() {
        return provider;
    }

    public ComponentBundle bundle() {
        return bundle;
    }

    public HologramTranslationStore translations() {
        return translations;
    }

    public Path getTranslationsPath() {
        return path;
    }

    private NBT.Builder base(final World world) {
        return NBT.builder()
                .registerTypeHierarchyAdapter(Billboard.class, new EnumAdapter<>(Billboard.class))
                .registerTypeHierarchyAdapter(BlockData.class, new BlockDataAdapter(getServer()))
                .registerTypeHierarchyAdapter(Brightness.class, new BrightnessAdapter())
                .registerTypeHierarchyAdapter(Color.class, new ColorAdapter())
                .registerTypeHierarchyAdapter(Component.class, new ComponentAdapter())
                .registerTypeHierarchyAdapter(EntityType.class, new EnumAdapter<>(EntityType.class))
                .registerTypeHierarchyAdapter(ItemDisplayTransform.class, new EnumAdapter<>(ItemDisplayTransform.class))
                .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
                .registerTypeHierarchyAdapter(LineType.class, new EnumAdapter<>(LineType.class))
                .registerTypeHierarchyAdapter(Matrix4f.class, new Matrix4fAdapter())
                .registerTypeHierarchyAdapter(Position.class, new PositionAdapter())
                .registerTypeHierarchyAdapter(Quaternionf.class, new QuaternionfAdapter())
                .registerTypeHierarchyAdapter(TextAlignment.class, new EnumAdapter<>(TextAlignment.class))
                .registerTypeHierarchyAdapter(TextColor.class, new TextColorAdapter())
                .registerTypeHierarchyAdapter(Transformation.class, new TransformationAdapter())
                .registerTypeHierarchyAdapter(Vector3f.class, new Vector3fAdapter());
    }

    public NBT serializer(final World world) {
        return base(world)
                .registerTypeHierarchyAdapter(BlockHologramLine.class, new BlockHologramLineSerializer())
                .registerTypeHierarchyAdapter(EntityHologramLine.class, new EntityHologramLineSerializer())
                .registerTypeHierarchyAdapter(ItemHologramLine.class, new ItemHologramLineSerializer())
                .registerTypeHierarchyAdapter(PagedHologramLine.class, new PagedHologramLineSerializer())
                .registerTypeHierarchyAdapter(TextHologramLine.class, new TextHologramLineSerializer())
                .build();
    }

    public NBT deserializer(final PaperHologram hologram) {
        return base(hologram.getWorld())
                .registerTypeHierarchyAdapter(PaperBlockHologramLine.class, new BlockHologramLineDeserializer(hologram))
                .registerTypeHierarchyAdapter(PaperEntityHologramLine.class, new EntityHologramLineDeserializer(hologram))
                .registerTypeHierarchyAdapter(PaperItemHologramLine.class, new ItemHologramLineDeserializer(hologram))
                .registerTypeHierarchyAdapter(PaperPagedHologramLine.class, new PagedHologramLineDeserializer(hologram))
                .registerTypeHierarchyAdapter(PaperTextHologramLine.class, new TextHologramLineDeserializer(hologram))
                .build();
    }

    public void loadHolograms(final World world) {
        final var dataFolder = hologramProvider().getDataFolder(world);
        if (!Files.isDirectory(dataFolder)) return;
        try (final var files = Files.find(dataFolder, 1, (path, attributes) -> {
            return attributes.isRegularFile() && path.getFileName().toString().endsWith(".dat");
        })) {
            files.forEach(path -> loadSafe(path, world));
        } catch (final IOException e) {
            getComponentLogger().error("Failed to load all holograms in world {}", world.getName(), e);
            getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            ERROR_TRACKER.trackError(e);
        }
    }

    public @Nullable Hologram loadSafe(final Path file, final World world) {
        try {
            try (final var inputStream = NBTInputStream.create(file)) {
                return load(inputStream, world);
            } catch (final Exception e) {
                final var backup = file.resolveSibling(file.getFileName() + "_old");
                if (!Files.isRegularFile(backup)) throw e;
                getComponentLogger().warn("Failed to load hologram from {}", file, e);
                getComponentLogger().warn("Falling back to {}", backup);
                try (final var inputStream = NBTInputStream.create(backup)) {
                    return load(inputStream, world);
                }
            }
        } catch (final ParserException e) {
            getComponentLogger().error("Failed to load hologram from {}", file, e);
        } catch (final EOFException e) {
            getComponentLogger().error("The hologram file {} is irrecoverably broken", file);
        } catch (final Exception e) {
            getComponentLogger().error("Failed to load hologram from {}", file, e);
            getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            ERROR_TRACKER.trackError(e);
        }
        return null;
    }

    private @Nullable Hologram load(final NBTInputStream inputStream, final World world) throws IOException {
        final var entry = inputStream.readNamedTag();
        final var name = entry.getKey();

        if (hologramProvider().hasHologram(name)) {
            getComponentLogger().warn("A hologram with the name '{}' is already loaded", name);
            return null;
        }

        final var hologram = new PaperHologram(this, name, world);
        hologram.deserialize(entry.getValue());
        hologramProvider().holograms.add(hologram);
        return hologram;
    }

    public void updateHologramTextLines(@Nullable final Player player) {
        final var holograms = player != null
                ? hologramProvider().getHolograms(player)
                : hologramProvider().getHolograms();
        holograms.map(PaperHologram.class::cast).forEach(hologram -> { // fixme: fix cast
            if (player == null) hologram.updateText();
            else hologram.updateText(player);
        });
    }
}
