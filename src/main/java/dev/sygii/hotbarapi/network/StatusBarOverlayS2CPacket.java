package dev.sygii.hotbarapi.network;

import dev.sygii.hotbarapi.HotbarAPI;
import net.minecraft.util.Identifier;

//? if =1.20.1 {
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public class StatusBarOverlayS2CPacket implements FabricPacket {
    public static final Identifier PACKET_ID = HotbarAPI.identifierOf( "register_status_bar_overlay_packet");

    public static final PacketType<StatusBarOverlayS2CPacket> TYPE = PacketType.create(
            PACKET_ID, StatusBarOverlayS2CPacket::new
    );

    protected final Identifier statusBarOverlayId;
    protected final Identifier targetId;
    protected final Identifier texture;
    protected final Identifier overlayLogicId;
    protected final Identifier overlayRendererId;
    protected final boolean underlay;

    public Identifier statusBarOverlayId() {
        return statusBarOverlayId;
    }

    public Identifier targetId() {
        return targetId;
    }

    public Identifier texture() {
        return texture;
    }

    public Identifier overlayLogicId() {
        return overlayLogicId;
    }

    public Identifier overlayRendererId() {
        return overlayRendererId;
    }

    public boolean underlay() {
        return underlay;
    }

    public StatusBarOverlayS2CPacket(PacketByteBuf buf) {
        this(buf.readIdentifier(), buf.readIdentifier(), buf.readIdentifier(), buf.readIdentifier(), buf.readIdentifier(), buf.readBoolean());
    }

    public StatusBarOverlayS2CPacket(Identifier statusBarOverlayId, Identifier targetId, Identifier texture, Identifier overlayLogicId, Identifier overlayRendererId, boolean underlay) {
        this.statusBarOverlayId = statusBarOverlayId;
        this.targetId = targetId;
        this.texture = texture;
        this.overlayLogicId = overlayLogicId;
        this.overlayRendererId = overlayRendererId;
        this.underlay = underlay;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(this.statusBarOverlayId);
        buf.writeIdentifier(this.targetId);
        buf.writeIdentifier(this.texture);
        buf.writeIdentifier(this.overlayLogicId);
        buf.writeIdentifier(this.overlayRendererId);
        buf.writeBoolean(this.underlay);
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


public record StatusBarOverlayS2CPacket(Identifier statusBarOverlayId, Identifier targetId, Identifier texture,
                                        Identifier overlayLogicId, Identifier overlayRendererId, boolean underlay) implements CustomPayload {
    public static final CustomPayload.Id<StatusBarOverlayS2CPacket> PACKET_ID = new CustomPayload.Id<>(HotbarAPI.identifierOf("register_status_bar_overlay_packet"));

    public static final PacketCodec<RegistryByteBuf, StatusBarOverlayS2CPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeIdentifier(value.statusBarOverlayId);
        buf.writeIdentifier(value.targetId);
        buf.writeIdentifier(value.texture);
        buf.writeIdentifier(value.overlayLogicId);
        buf.writeIdentifier(value.overlayRendererId);
        buf.writeBoolean(value.underlay);
    }, buf -> new StatusBarOverlayS2CPacket(buf.readIdentifier(), buf.readIdentifier(), buf.readIdentifier(), buf.readIdentifier(), buf.readIdentifier(), buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
*///?}