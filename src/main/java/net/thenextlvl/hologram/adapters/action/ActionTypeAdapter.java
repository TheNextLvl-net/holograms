package net.thenextlvl.hologram.adapters.action;

import net.thenextlvl.hologram.action.ActionType;
import net.thenextlvl.hologram.action.ActionTypeRegistry;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.StringTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ActionTypeAdapter implements TagAdapter<ActionType<?>> {
    @Override
    public ActionType<?> deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        return ActionTypeRegistry.registry().getByName(tag.getAsString()).orElseThrow(() -> {
            return new ParserException("Unknown action type: " + tag.getAsString());
        });
    }

    @Override
    public Tag serialize(final ActionType<?> type, final TagSerializationContext context) throws ParserException {
        return StringTag.of(type.name());
    }
}
