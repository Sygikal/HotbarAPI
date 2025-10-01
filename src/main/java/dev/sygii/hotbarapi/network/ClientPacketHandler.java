package dev.sygii.hotbarapi.network;

import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.HotbarAPIClient;
import dev.sygii.hotbarapi.elements.StatusBar;
import dev.sygii.hotbarapi.elements.StatusBarLogic;
import dev.sygii.hotbarapi.elements.StatusBarOverlay;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import dev.sygii.hotbarapi.network.packet.HotbarHighlightPacket;
import dev.sygii.hotbarapi.network.packet.ResetStatusBarsS2CPacket;
import dev.sygii.hotbarapi.network.packet.StatusBarOverlayS2CPacket;
import dev.sygii.hotbarapi.network.packet.StatusBarS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

//? if >1.20.1 {
/*import net.minecraft.network.packet.CustomPayload;
*///?}

public class ClientPacketHandler {

    public static void init() {
        registerPacket(HotbarHighlightPacket.PACKET_ID, new PacketRunner<HotbarHighlightPacket>() {
            @Override
            public void run(HotbarHighlightPacket packet, MinecraftClient client) {
                client.execute(() -> {
                    HotbarAPIClient.mappedHotbarHighlights.put(packet.slot(), HotbarAPIClient.hotbarHighlights.get(packet.highlight()));
                });
            }
        },
        (buf) -> {
            //? if =1.20.1 {
            return new HotbarHighlightPacket((PacketByteBuf) buf);
            //?} else {
            /*return null;
            *///?}
        });

        registerPacket(ResetStatusBarsS2CPacket.PACKET_ID, new PacketRunner<ResetStatusBarsS2CPacket>() {
            @Override
            public void run(ResetStatusBarsS2CPacket packet, MinecraftClient client) {
                HotbarAPIClient.statusBars.clear();
                HotbarAPIClient.replacedStatusBars.clear();
            }
        },
        (buf) -> {
            //? if =1.20.1 {
            return new ResetStatusBarsS2CPacket((PacketByteBuf) buf);
            //?} else {
            /*return null;
             *///?}
        });

        registerPacket(StatusBarS2CPacket.PACKET_ID, new PacketRunner<StatusBarS2CPacket>() {
            @Override
            public void run(StatusBarS2CPacket payload, MinecraftClient client) {
                Identifier statusBarId = payload.statusBarId();
                List<Identifier> beforeIds = payload.beforeIds();
                List<Identifier> afterIds = payload.afterIds();
                Identifier toReplace = payload.toReplace();
                Identifier texture = payload.texture();
                StatusBarRenderer.Direction direction = payload.direction();
                StatusBarRenderer.Position position = payload.position();
                Identifier statusBarLogicId = payload.statusBarLogicId();
                Identifier statusBarRendererId = payload.statusBarRendererId();
                EnumSet<GameMode> gameModes = payload.gameModes();
                client.execute(() -> {
                    HotbarAPI.registerStatusBar(new StatusBar(statusBarId, HotbarAPI.getRenderer(statusBarRendererId).update(texture, position, direction), HotbarAPI.getLogic(statusBarLogicId), beforeIds, afterIds, toReplace, gameModes));
                });
            }
        }, (buf) -> {
            //? if =1.20.1 {
            return new StatusBarS2CPacket((PacketByteBuf) buf);
            //?} else {
            /*return null;
             *///?}
        });

        registerPacket(StatusBarOverlayS2CPacket.PACKET_ID, new PacketRunner<StatusBarOverlayS2CPacket>() {
            @Override
            public void run(StatusBarOverlayS2CPacket payload, MinecraftClient client) {
                Identifier overlayBarId = payload.statusBarOverlayId();
                Identifier targetId = payload.targetId();
                Identifier texture = payload.texture();
                Identifier overlayRendererId = payload.overlayRendererId();
                Identifier overlayLogicId = payload.overlayLogicId();
                boolean underlay = payload.underlay();
                client.execute(() -> {
                    HotbarAPI.registerStatusBarOverlay(targetId, new StatusBarOverlay(overlayBarId, targetId, texture, HotbarAPI.getRenderer(overlayRendererId), HotbarAPI.getLogic(overlayLogicId), underlay));
                });
            }
        }, (buf) -> {
            //? if =1.20.1 {
            return new StatusBarOverlayS2CPacket((PacketByteBuf) buf);
            //?} else {
            /*return null;
             *///?}
        });
    }

    public static <T> void registerPacket(Object id, PacketRunner<T> runner, PacketRetriever<T> retriever) {
        //? if =1.20.1 {
        ClientPlayNetworking.registerGlobalReceiver((Identifier) id, (client, handler, buf, sender) -> {
            T payload = retriever.run(buf);
            runner.run((T) payload, client);
        });
		//?} else {
        /*ClientPlayNetworking.registerGlobalReceiver((CustomPayload.Id) id, (payload, context) -> {
            runner.run((T) payload, context.client());
        });
        *///?}
    }

    public interface PacketRunner<T> {
        void run(T payload, MinecraftClient client);
    }

    public interface PacketRetriever<T> {
        T run(Object buf);
    }

}
