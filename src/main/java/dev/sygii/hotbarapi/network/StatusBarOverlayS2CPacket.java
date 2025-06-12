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

    public Identifier getStatusBarOverlayId() {
        return statusBarOverlayId;
    }

    public Identifier getTargetId() {
        return targetId;
    }

    public Identifier getTexture() {
        return texture;
    }

    public Identifier getOverlayLogicId() {
        return overlayLogicId;
    }

    public Identifier getOverlayRendererId() {
        return overlayRendererId;
    }

    public boolean isUnderlay() {
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
