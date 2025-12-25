import org.jspecify.annotations.NullMarked;

@NullMarked
module net.thenextlvl.holograms {
    exports net.thenextlvl.hologram.line;
    exports net.thenextlvl.hologram;

    requires net.kyori.adventure;
    requires net.kyori.examination.api;
    requires org.bukkit;
    requires org.joml;

    requires static org.jetbrains.annotations;
    requires static org.jspecify;
}