package dev.sygii.hotbarapi.network;

import dev.sygii.hotbarapi.HotbarAPI;
import net.minecraft.network.PacketByteBuf;
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
