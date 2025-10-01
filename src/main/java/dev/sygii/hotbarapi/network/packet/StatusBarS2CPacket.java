package dev.sygii.hotbarapi.network.packet;

import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import net.minecraft.network.PacketByteBuf;
import java.util.EnumSet;
import java.util.List;

//? if =1.20.1 {
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;

public class StatusBarS2CPacket implements FabricPacket {
    public static final Identifier PACKET_ID = HotbarAPI.identifierOf( "register_status_bar_packet");

    public static final PacketType<StatusBarS2CPacket> TYPE = PacketType.create(
            PACKET_ID, StatusBarS2CPacket::new
    );

    public Identifier statusBarId() {
        return statusBarId;
    }

    public List<Identifier> afterIds() {
        return afterIds;
    }

    public Identifier toReplace() {
        return toReplace;
    }

    public Identifier texture() {
        return texture;
    }

    public StatusBarRenderer.Direction direction() {
        return direction;
    }

    public StatusBarRenderer.Position position() {
        return position;
    }

    public Identifier statusBarRendererId() {
        return statusBarRendererId;
    }

    public EnumSet<GameMode> gameModes() {
        return gameModes;
    }

    public Identifier statusBarLogicId() {
        return statusBarLogicId;
    }

    public List<Identifier> beforeIds() {
        return beforeIds;
    }

    protected final Identifier statusBarId;
    protected final List<Identifier> beforeIds;
    protected final List<Identifier> afterIds;
    protected final Identifier toReplace;
    protected final Identifier texture;
    protected final StatusBarRenderer.Direction direction;
    protected final StatusBarRenderer.Position position;
    protected final Identifier statusBarLogicId;
    protected final Identifier statusBarRendererId;
    protected final EnumSet<GameMode> gameModes;

    public StatusBarS2CPacket(PacketByteBuf buf) {
        this(buf.readIdentifier(), buf.readList(PacketByteBuf::readIdentifier), buf.readList(PacketByteBuf::readIdentifier), buf.readIdentifier(), buf.readIdentifier(), buf.readEnumConstant(StatusBarRenderer.Direction.class), buf.readEnumConstant(StatusBarRenderer.Position.class), buf.readIdentifier(), buf.readIdentifier(), buf.readEnumSet(GameMode.class));
    }

    public StatusBarS2CPacket(Identifier statusBarId, List<Identifier> beforeIds, List<Identifier> afterIds, Identifier toReplace, Identifier texture, StatusBarRenderer.Direction direction, StatusBarRenderer.Position position, Identifier statusBarLogicId, Identifier statusBarRendererId, EnumSet<GameMode> gameModes) {
        this.statusBarId = statusBarId;
        this.beforeIds = beforeIds;
        this.afterIds = afterIds;
        this.toReplace = toReplace;
        this.texture = texture;
        this.direction = direction;
        this.position = position;
        this.statusBarLogicId = statusBarLogicId;
        this.statusBarRendererId = statusBarRendererId;
        this.gameModes = gameModes;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(this.statusBarId);
        buf.writeCollection(this.beforeIds,  PacketByteBuf::writeIdentifier);
        buf.writeCollection(this.afterIds,  PacketByteBuf::writeIdentifier);
        buf.writeIdentifier(this.toReplace);
        buf.writeIdentifier(this.texture);
        buf.writeEnumConstant(this.direction);
        buf.writeEnumConstant(this.position);
        buf.writeIdentifier(this.statusBarLogicId);
        buf.writeIdentifier(this.statusBarRendererId);
        buf.writeEnumSet(this.gameModes, GameMode.class);
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

public record StatusBarS2CPacket(Identifier statusBarId, List<Identifier> beforeIds, List<Identifier> afterIds, Identifier toReplace,
                                 Identifier texture, StatusBarRenderer.Direction direction, StatusBarRenderer.Position position,
                                 Identifier statusBarLogicId, Identifier statusBarRendererId, EnumSet<GameMode> gameModes) implements CustomPayload {

    public static final CustomPayload.Id<StatusBarS2CPacket> PACKET_ID = new CustomPayload.Id<>(HotbarAPI.identifierOf("register_status_bar_packet"));

    public static final PacketCodec<RegistryByteBuf, StatusBarS2CPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeIdentifier(value.statusBarId);
        buf.writeCollection(value.beforeIds,  PacketByteBuf::writeIdentifier);
        buf.writeCollection(value.afterIds,  PacketByteBuf::writeIdentifier);
        buf.writeIdentifier(value.toReplace);
        buf.writeIdentifier(value.texture);
        buf.writeEnumConstant(value.direction);
        buf.writeEnumConstant(value.position);
        buf.writeIdentifier(value.statusBarLogicId);
        buf.writeIdentifier(value.statusBarRendererId);
        buf.writeEnumSet(value.gameModes, GameMode.class);
    }, buf -> new StatusBarS2CPacket(buf.readIdentifier(), buf.readList(PacketByteBuf::readIdentifier), buf.readList(PacketByteBuf::readIdentifier), buf.readIdentifier(), buf.readIdentifier(), buf.readEnumConstant(StatusBarRenderer.Direction.class), buf.readEnumConstant(StatusBarRenderer.Position.class), buf.readIdentifier(), buf.readIdentifier(), buf.readEnumSet(GameMode.class)));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
*///?}