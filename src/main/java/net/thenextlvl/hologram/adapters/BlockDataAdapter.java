package net.thenextlvl.hologram.adapters;

import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.StringTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.Server;
import org.bukkit.block.data.BlockData;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class BlockDataAdapter implements TagAdapter<BlockData> {
    private final Server server;

    public BlockDataAdapter(Server server) {
        this.server = server;
    }

    @Override
    public BlockData deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return server.createBlockData(tag.getAsString());
    }

    @Override
    public Tag serialize(BlockData blockData, TagSerializationContext context) throws ParserException {
        return StringTag.of(blockData.getAsString());
    }
}
