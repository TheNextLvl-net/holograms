package net.thenextlvl.hologram.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.bukkit.Color;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ColorArgumentType implements CustomArgumentType.Converted<Color, String> {
    @Override
    public Color convert(String nativeType) {
        if (nativeType.length() != 8) {
            throw new IllegalStateException("Color hex must be 8 characters long");
        }
        var argb = Long.decode("0x" + nativeType);
        return Color.fromARGB(argb.intValue());
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }
}
