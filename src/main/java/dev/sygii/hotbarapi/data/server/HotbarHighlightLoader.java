package dev.sygii.hotbarapi.data.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.elements.HotbarHighlight;
import dev.sygii.ultralib.data.loader.SimpleDataLoader;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HotbarHighlightLoader extends SimpleDataLoader {
//? if <1.21.9 {
   @Override
    public Identifier getFabricId() {
        return ID;
    }
//?}

    public static final Identifier ID = HotbarAPI.identifierOf("hotbar_highlight_loader");

    public HotbarHighlightLoader() {
        super(ID, "hotbar_highlight");
    }

    public void reloadResource(JsonObject data, Identifier entryId, String fileName) {
        Identifier texture = null;
        if (data.has("texture")) {
            texture = Identifier.tryParse(data.get("texture").getAsString());
        }

        Color color = Color.decode(data.get("color").getAsString());

        HotbarAPI.hotbarHighlights.put(entryId, new HotbarHighlight(entryId, texture, color));
    }
}