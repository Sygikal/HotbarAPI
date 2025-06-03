package dev.sygii.hotbarapi.network;

import dev.sygii.hotbarapi.HotbarAPI;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class HotbarHighlightPacket implements FabricPacket {
    public static final Identifier PACKET_ID = HotbarAPI.identifierOf( "hotbar_highlight_packet");
    protected final int slot;
    //protected final HotbarHighlight highlight;
    protected final Identifier highlight;

    public static final PacketType<HotbarHighlightPacket> TYPE = PacketType.create(
            PACKET_ID, HotbarHighlightPacket::new
    );

    /*public HotbarHighlightPacket(PacketByteBuf buf) {
        this(buf.readInt(), HotbarHighlight.read(buf));
    }

    public HotbarHighlightPacket(int slot, HotbarHighlight highlight) {
        this.slot = slot;
        this.highlight = highlight;
    }*/

    public HotbarHighlightPacket(PacketByteBuf buf) {
        this(buf.readInt(), buf.readIdentifier());
    }

    public HotbarHighlightPacket(int slot, Identifier highlight) {
        this.slot = slot;
        this.highlight = highlight;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.slot);
        buf.writeIdentifier(this.highlight);
        //this.highlight.write(buf);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public int slot() {
        return slot;
    }

    public Identifier highlight() {
        return this.highlight;
    }

    /*public HotbarHighlight highlight() {
        return highlight;
    }*/
}
