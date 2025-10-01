package dev.sygii.hotbarapi.network.packet;

//? if =1.20.1 {
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
//?} else {
/*import dev.sygii.hotbarapi.HotbarAPI;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record HotbarHighlightPacket(int slot, Identifier highlight) implements CustomPayload {
    public static final CustomPayload.Id<HotbarHighlightPacket> PACKET_ID = new CustomPayload.Id<>(HotbarAPI.identifierOf("hotbar_highlight_packet"));

    public static final PacketCodec<RegistryByteBuf, HotbarHighlightPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeInt(value.slot);
        buf.writeIdentifier(value.highlight);
    }, buf -> new HotbarHighlightPacket(buf.readInt(), buf.readIdentifier()));


    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
*///?}
