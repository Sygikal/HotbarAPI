package dev.sygii.hotbarapi.network;

import dev.sygii.hotbarapi.HotbarAPI;
import net.minecraft.util.Identifier;

//? if =1.20.1 {
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public class ResetStatusBarsS2CPacket implements FabricPacket {
    public static final Identifier PACKET_ID = HotbarAPI.identifierOf( "reset_status_bars_packet");

    public static final PacketType<ResetStatusBarsS2CPacket> TYPE = PacketType.create(
            PACKET_ID, ResetStatusBarsS2CPacket::new
    );

    public ResetStatusBarsS2CPacket(PacketByteBuf buf) {
        this();
    }

    public ResetStatusBarsS2CPacket() {

    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
//?} else {
/*import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ResetStatusBarsS2CPacket() implements CustomPayload {
    public static final CustomPayload.Id<ResetStatusBarsS2CPacket> PACKET_ID = new CustomPayload.Id<>(HotbarAPI.identifierOf("reset_status_bars_packet"));

    public static final PacketCodec<RegistryByteBuf, ResetStatusBarsS2CPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
    }, buf -> new ResetStatusBarsS2CPacket());


    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
*///?}
