package net.thenextlvl.hologram.adapters;

import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.entity.Display;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class BrightnessAdapter implements TagAdapter<Display.Brightness> {
    @Override
    public Display.Brightness deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var root = tag.getAsCompound();
        var blockLight = root.get("blockLight").getAsInt();
        var skyLight = root.get("skyLight").getAsInt();
        return new Display.Brightness(blockLight, skyLight);
    }

    @Override
    public Tag serialize(Display.Brightness object, TagSerializationContext context) throws ParserException {
        return CompoundTag.builder()
                .put("blockLight", object.getBlockLight())
                .put("skyLight", object.getSkyLight())
                .build();
    }
}
