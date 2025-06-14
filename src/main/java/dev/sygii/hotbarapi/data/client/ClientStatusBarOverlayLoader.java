package dev.sygii.hotbarapi.data.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.HotbarAPIClient;
import dev.sygii.hotbarapi.elements.StatusBar;
import dev.sygii.hotbarapi.elements.StatusBarLogic;
import dev.sygii.hotbarapi.elements.StatusBarOverlay;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import dev.sygii.hotbarapi.network.StatusBarOverlayS2CPacket;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;

public class ClientStatusBarOverlayLoader implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return HotbarAPI.identifierOf("client_status_bar_overlay_loader");
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return Collections.singleton(HotbarAPI.identifierOf("client_status_bar_loader"));
    }

    @Override
    public void reload(ResourceManager manager) {
        manager.findResources("status_bar_overlay", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                InputStream stream = null;
                stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                String statusBarOverlayId = getBaseName(id.getPath());
                Identifier targetId = Identifier.tryParse(data.get("target").getAsString());
                Identifier texture = Identifier.tryParse(data.get("texture").getAsString());
                Identifier overlayId = Identifier.of(id.getNamespace(), statusBarOverlayId);

                StatusBarLogic logic = HotbarAPI.DEFAULT_STATUS_BAR_LOGIC;
                if (data.has("logic") && HotbarAPI.statusBarLogics.get(Identifier.tryParse(data.get("logic").getAsString())) != null) {
                    logic = HotbarAPI.statusBarLogics.get(Identifier.tryParse(data.get("logic").getAsString()));
                }

                StatusBarRenderer renderer = HotbarAPI.DEFAULT_STATUS_BAR_RENDERER;
                if (data.has("renderer") && HotbarAPI.statusBarRenderers.get(Identifier.tryParse(data.get("renderer").getAsString())) != null) {
                    renderer = HotbarAPI.statusBarRenderers.get(Identifier.tryParse(data.get("renderer").getAsString()));
                }

                boolean underlay = false;
                if (data.has("underlay")) {
                    underlay = data.get("underlay").getAsBoolean();
                }

                StatusBar targetBar = null;
				for (StatusBar bar : HotbarAPIClient.statusBars) {
					if (bar.getId().equals(targetId)) {
						targetBar = bar;
						break;
					}
				}

				if (targetBar != null) {
					StatusBarOverlay overlay = new StatusBarOverlay(overlayId, targetId, renderer.update(texture, targetBar.getRenderer().getPosition(), targetBar.getRenderer().getDirection()), logic, underlay);
					if (underlay) {
						targetBar.addUnderlay(overlay);
					}else {
						targetBar.addOverlay(overlay);
					}
				}

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static String getBaseName(String filename) {
        if (filename == null)
            return null;

        String name = new File(filename).getName();
        int extPos = name.lastIndexOf('.');

        if (extPos < 0)
            return name;

        return name.substring(0, extPos);
    }
}
