package net.thenextlvl.hologram.adapters.deserializers;

import net.thenextlvl.hologram.line.DisplayHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.util.Transformation;
import org.joml.Matrix4f;
import org.jspecify.annotations.NullMarked;

@NullMarked
abstract class DisplayHologramLineDeserializer<T extends DisplayHologramLine<T, ?>> extends HologramLineDeserializer<T> {
    protected DisplayHologramLineDeserializer(final PaperHologram hologram) {
        super(hologram);
    }

    @Override
    protected void deserialize(final T line, final CompoundTag tag, final TagDeserializationContext context) throws ParserException {
        tag.optional("viewRange").map(Tag::getAsFloat).ifPresent(line::setViewRange);
        tag.optional("displayWidth").map(Tag::getAsFloat).ifPresent(line::setDisplayWidth);
        tag.optional("displayHeight").map(Tag::getAsFloat).ifPresent(line::setDisplayHeight);
        tag.optional("transformation").map(tag1 -> context.deserialize(tag1, Transformation.class)).ifPresent(line::setTransformation);
        tag.optional("transformationMatrix").map(tag1 -> context.deserialize(tag1, Matrix4f.class)).ifPresent(line::setTransformationMatrix);
        tag.optional("billboard").map(tag1 -> context.deserialize(tag1, Billboard.class)).ifPresent(line::setBillboard);
        tag.optional("brightness").map(tag1 -> context.deserialize(tag1, Brightness.class)).ifPresent(line::setBrightness);
        tag.optional("glowColorOverride").map(tag1 -> context.deserialize(tag1, Color.class)).ifPresent(line::setGlowColorOverride);
        tag.optional("interpolationDelay").map(Tag::getAsInt).ifPresent(line::setInterpolationDelay);
        tag.optional("interpolationDuration").map(Tag::getAsInt).ifPresent(line::setInterpolationDuration);
        tag.optional("shadowRadius").map(Tag::getAsFloat).ifPresent(line::setShadowRadius);
        tag.optional("shadowStrength").map(Tag::getAsFloat).ifPresent(line::setShadowStrength);
        tag.optional("teleportDuration").map(Tag::getAsInt).ifPresent(line::setTeleportDuration);
    }
}
