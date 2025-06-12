package dev.sygii.hotbarapi.network;

import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.util.EnumSet;
import java.util.List;

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
