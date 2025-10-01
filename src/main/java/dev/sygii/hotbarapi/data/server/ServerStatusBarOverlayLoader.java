package dev.sygii.hotbarapi.data.server;

import com.google.gson.JsonObject;
import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.network.packet.StatusBarOverlayS2CPacket;
import dev.sygii.ultralib.data.loader.SimpleDataLoader;
import dev.sygii.ultralib.data.util.OptionalObject;
import net.minecraft.util.Identifier;

public class ServerStatusBarOverlayLoader extends SimpleDataLoader {
    //? if <1.21.9 {
   @Override
    public Identifier getFabricId() {
        return ID;
    }
//?}
    public static final Identifier ID = HotbarAPI.identifierOf("server_status_bar_overlay_loader");

    public ServerStatusBarOverlayLoader() {
        super(ID, "status_bar_overlay");
    }

    public void preReload() {
        HotbarAPI.statusBarOverlayPacketQueue.clear();
    }

    public void reloadResource(JsonObject data, Identifier entryId, String fileName) {
        Identifier target = Identifier.tryParse(data.get("target").getAsString());
        Identifier texture = Identifier.tryParse(data.get("texture").getAsString());

        Identifier logicId = Identifier.tryParse(OptionalObject.get(data, "logic", HotbarAPI.DEFAULT_STATUS_BAR_LOGIC.getId().toString()).getAsString());
        Identifier rendererId = Identifier.tryParse(OptionalObject.get(data, "renderer", HotbarAPI.DEFAULT_STATUS_BAR_RENDERER.getId().toString()).getAsString());

        boolean underlay = OptionalObject.get(data, "underlay", false).getAsBoolean();

        HotbarAPI.statusBarOverlayPacketQueue.add(new StatusBarOverlayS2CPacket(entryId, target, texture, logicId, rendererId, underlay));
    }
}
