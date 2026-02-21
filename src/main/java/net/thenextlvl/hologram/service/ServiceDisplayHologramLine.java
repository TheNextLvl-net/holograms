package net.thenextlvl.hologram.service;

import net.thenextlvl.hologram.line.DisplayHologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
abstract class ServiceDisplayHologramLine<T, L extends DisplayHologramLine> extends ServiceHologramLine<T, L> {
    protected ServiceDisplayHologramLine(final L line) {
        super(line);
    }

    @Override
    public final double getOffsetX() {
        return line.getTransformation().getTranslation().x();
    }

    @Override
    public final double getOffsetY() {
        return line.getTransformation().getTranslation().y();
    }

    @Override
    public final double getOffsetZ() {
        return line.getTransformation().getTranslation().z();
    }

    @Override
    public final void setOffsetX(final double offsetX) {
        final var transformation = line.getTransformation();
        transformation.getTranslation().x = (float) offsetX;
        line.setTransformation(transformation);
    }

    @Override
    public final void setOffsetY(final double offsetY) {
        final var transformation = line.getTransformation();
        transformation.getTranslation().y = (float) offsetY;
        line.setTransformation(transformation);
    }

    @Override
    public final void setOffsetZ(final double offsetZ) {
        final var transformation = line.getTransformation();
        transformation.getTranslation().z = (float) offsetZ;
        line.setTransformation(transformation);
    }
}
