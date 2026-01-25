package net.thenextlvl.hologram.adapters.serializers;

import net.thenextlvl.hologram.line.DisplayHologramLine;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import org.jspecify.annotations.NullMarked;

@NullMarked
abstract class DisplayHologramLineSerializer<T extends DisplayHologramLine<T, ?>> extends HologramLineSerializer<T> {
    @Override
    public CompoundTag serialize(final T line, final TagSerializationContext context) throws ParserException {
        final var builder = CompoundTag.builder();
        line.getBrightness().map(context::serialize).ifPresent(tag -> builder.put("brightness", tag));
        return builder.putAll(super.serialize(line, context))
                .put("billboard", context.serialize(line.getBillboard()))
                .put("height", line.getDisplayHeight())
                .put("interpolationDelay", line.getInterpolationDelay())
                .put("interpolationDuration", line.getInterpolationDuration())
                .put("shadowRadius", line.getShadowRadius())
                .put("shadowStrength", line.getShadowStrength())
                .put("teleportDuration", line.getTeleportDuration())
                .put("transformation", context.serialize(line.getTransformation()))
                .put("viewRange", line.getViewRange())
                .put("width", line.getDisplayWidth())
                .build();
    }
}   
        
