package net.thenextlvl.hologram.service;

import net.thenextlvl.hologram.line.TextHologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ServiceTextHologramLine extends ServiceDisplayHologramLine<String, TextHologramLine> {
    public ServiceTextHologramLine(final TextHologramLine line) {
        super(line);
    }

    @Override
    public String getContent() {
        return line.getUnparsedText().orElse("");
    }

    @Override
    public void setContent(final String content) {
        line.setUnparsedText(content);
    }
}
