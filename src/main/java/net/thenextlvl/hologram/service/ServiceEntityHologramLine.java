package net.thenextlvl.hologram.service;

import net.thenextlvl.hologram.line.EntityHologramLine;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ServiceEntityHologramLine extends ServiceHologramLine<EntityType, EntityHologramLine> {
    public ServiceEntityHologramLine(final EntityHologramLine line) {
        super(line);
    }

    @Override
    public EntityType getContent() {
        return line.getEntityType();
    }

    @Override
    public double getOffsetX() {
        return line.getOffset().x();
    }

    @Override
    public double getOffsetY() {
        return line.getOffset().y();
    }

    @Override
    public double getOffsetZ() {
        return line.getOffset().z();
    }

    @Override
    public void setContent(final EntityType content) {
        line.setEntityType(content);
    }

    @Override
    public void setOffsetX(final double offsetX) {
        final var offset = line.getOffset();
        offset.x = (float) offsetX;
        line.setOffset(offset);
    }

    @Override
    public void setOffsetY(final double offsetY) {
        final var offset = line.getOffset();
        offset.y = (float) offsetY;
        line.setOffset(offset);
    }

    @Override
    public void setOffsetZ(final double offsetZ) {
        final var offset = line.getOffset();
        offset.z = (float) offsetZ;
        line.setOffset(offset);
    }
}
