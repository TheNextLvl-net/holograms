import org.jspecify.annotations.NullMarked;

@NullMarked
module net.thenextlvl.holograms {
    exports net.thenextlvl.hologram.action;
    exports net.thenextlvl.hologram.event;
    exports net.thenextlvl.hologram.image;
    exports net.thenextlvl.hologram.line;
    exports net.thenextlvl.hologram;

    requires com.google.common;
    requires java.desktop;
    requires net.kyori.adventure.key;
    requires net.kyori.adventure.text.minimessage;
    requires net.kyori.adventure;
    requires net.kyori.examination.api;
    requires net.thenextlvl.binder;
    requires org.bukkit;
    requires org.joml;
    requires org.slf4j;

    requires static org.jetbrains.annotations;
    requires static org.jspecify;
}