package dev.sygii.hotbarapi.network;

import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.network.packet.HotbarHighlightPacket;
import dev.sygii.hotbarapi.network.packet.ResetStatusBarsS2CPacket;
import dev.sygii.hotbarapi.network.packet.StatusBarOverlayS2CPacket;
import dev.sygii.hotbarapi.network.packet.StatusBarS2CPacket;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

//? if >1.20.1 {
/*import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
*///?}

public class ServerPacketHandler {

    public static void init() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(HotbarAPI.identifierOf("sync_status_bars"), (player, joined) -> {
            ServerPlayNetworking.send(player, new ResetStatusBarsS2CPacket());
            HotbarAPI.statusBarPacketQueue.forEach((packet) -> ServerPlayNetworking.send(player, packet));
            HotbarAPI.statusBarOverlayPacketQueue.forEach((packet) -> ServerPlayNetworking.send(player, packet));
        });

        //? if >1.20.1 {
        /*PayloadTypeRegistry.playS2C().register(HotbarHighlightPacket.PACKET_ID, HotbarHighlightPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(ResetStatusBarsS2CPacket.PACKET_ID, ResetStatusBarsS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(StatusBarS2CPacket.PACKET_ID, StatusBarS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(StatusBarOverlayS2CPacket.PACKET_ID, StatusBarOverlayS2CPacket.PACKET_CODEC);
        *///?}
    }
}
