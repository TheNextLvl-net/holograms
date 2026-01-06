package net.thenextlvl.hologram.adapters.serializers;

import net.thenextlvl.hologram.line.DisplayHologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import org.jspecify.annotations.NullMarked;

@NullMarked
abstract class DisplayHologramLineSerializer<T extends DisplayHologramLine<T, ?>> extends HologramLineSerializer<T> {
    @Override
    public CompoundTag serialize(T line, TagSerializationContext context) throws ParserException {
        var builder = CompoundTag.builder();
        line.getGlowColorOverride().map(context::serialize).ifPresent(tag -> builder.put("glowColorOverride", tag));
        line.getBrightness().map(context::serialize).ifPresent(tag -> builder.put("brightness", tag));
        return builder.putAll(super.serialize(line, context))
                .put("height", line.getDisplayHeight())
                .put("width", line.getDisplayWidth())
                .put("shadowRadius", line.getShadowRadius())
                .put("shadowStrength", line.getShadowStrength())
                .put("viewRange", line.getViewRange())
                .put("interpolationDelay", line.getInterpolationDelay())
                .put("interpolationDuration", line.getInterpolationDuration())
                .put("teleportDuration", line.getTeleportDuration())
                .put("billboard", context.serialize(line.getBillboard()))
                .put("transformation", context.serialize(line.getTransformation()))
                .build();
    }
}   
        
