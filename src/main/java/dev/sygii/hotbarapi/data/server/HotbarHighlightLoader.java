package dev.sygii.hotbarapi.data.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.elements.HotbarHighlight;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HotbarHighlightLoader implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return HotbarAPI.identifierOf("hotbar_highlight_loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        //HotbarAPI.hotbarHighlights.clear();
        manager.findResources("hotbar_highlight", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                InputStream stream = null;
                stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                String hotbarHighlightId = getBaseName(id.getPath());

                Identifier texture = null;
                if (data.has("texture")) {
                    texture = Identifier.tryParse(data.get("texture").getAsString());
                }

                Color color = Color.decode(data.get("color").getAsString());

                Identifier hotbarId = Identifier.of(id.getNamespace(), hotbarHighlightId);
                HotbarAPI.hotbarHighlights.put(hotbarId, new HotbarHighlight(hotbarId, texture, color));
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
