package dev.sygii.hotbarapi.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.elements.HotbarHighlight;
import dev.sygii.hotbarapi.elements.StatusBar;
import dev.sygii.hotbarapi.elements.StatusBarLogic;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import dev.sygii.hotbarapi.util.ColorUtil;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.stat.Stat;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

public class StatusBarLoader implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return HotbarAPI.identifierOf("status_bar_loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        HotbarAPI.hotbarHighlights.clear();
        manager.findResources("status_bar", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                InputStream stream = null;
                stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                int index = -1;
                if (data.has("index")) {
                    index = data.get("index").getAsInt();

                }

                String statusBarId = getBaseName(id.getPath());
                List<Identifier> beforeIds = new ArrayList<>();
                if (data.has("before")) {
                    for (JsonElement elem : data.getAsJsonArray("before")) {
                        beforeIds.add(Identifier.tryParse(elem.getAsString()));
                    }
                    //beforeIds = Identifier.tryParse(data.get("before").getAsString());
                }

                List<Identifier> afterIds = new ArrayList<>();
                if (data.has("after")) {
                    for (JsonElement elem : data.getAsJsonArray("after")) {
                        afterIds.add(Identifier.tryParse(elem.getAsString()));
                    }
                    //beforeIds = Identifier.tryParse(data.get("before").getAsString());
                }

                Identifier replaceId;
                boolean replace = false;
                if (data.has("replace")) {
                    replace = true;
                    replaceId = Identifier.tryParse(data.get("replace").getAsString());
                } else {
                    replaceId = null;
                }
                Identifier texture = Identifier.tryParse(data.get("texture").getAsString());
                Identifier barId = Identifier.of(id.getNamespace(), statusBarId);
                StatusBarRenderer.Direction direction = StatusBarRenderer.Direction.valueOf(data.get("direction").getAsString().toUpperCase());
                StatusBarRenderer.Position position = StatusBarRenderer.Position.valueOf(data.get("position").getAsString().toUpperCase());

                Identifier logicId = Identifier.tryParse(data.get("logic").getAsString());
                StatusBarLogic logic = HotbarAPI.statusBarLogics.get(logicId);

                Identifier rendererId = Identifier.tryParse(data.get("renderer").getAsString());
                StatusBarRenderer renderer = HotbarAPI.statusBarRenderers.get(rendererId);
                StatusBarRenderer defaultRenderer = new StatusBarRenderer(HotbarAPI.identifierOf("default"), texture, position, direction);

                /*if (replace) {
                    HotbarAPI.replaceStatusBar(replaceId, new StatusBar(barId, renderer == null ? defaultRenderer : renderer, logic == null ? HotbarAPI.defaultLogic : logic, important));
                } else if(before) {
                    HotbarAPI.addStatusBarBefore(beforeId, new StatusBar(barId, renderer == null ? defaultRenderer : renderer, logic == null ? HotbarAPI.defaultLogic : logic, important));
                }else {*/
                /*if (index != -1) {
                    if (index > HotbarAPI.statusBars.stream().filter((bar) -> bar.getRenderer().getPosition().equals(position)).toList().size()) {
                        index = HotbarAPI.statusBars.stream().filter((bar) -> bar.getRenderer().getPosition().equals(position)).toList().size();
                    }
                    System.out.println(statusBarId + "  |  " + index);
                    HotbarAPI.addStatusBar(new StatusBar(barId, renderer == null ? defaultRenderer : renderer, logic == null ? HotbarAPI.defaultLogic : logic, beforeIds, afterIds, important), index);
                }else {
                    HotbarAPI.addStatusBar(new StatusBar(barId, renderer == null ? defaultRenderer : renderer, logic == null ? HotbarAPI.defaultLogic : logic, beforeIds, afterIds, important));
                }*/

                List<GameMode> gameModes = new ArrayList<>();
                if (data.has("gamemodes")) {
                    for (JsonElement elem : data.getAsJsonArray("gamemodes")) {
                        gameModes.add(GameMode.valueOf(elem.getAsString().toUpperCase()));
                    }
                }else {
                    gameModes.add(GameMode.SURVIVAL);
                    gameModes.add(GameMode.ADVENTURE);
                }
                if (replace) {
                    HotbarAPI.statusBars.removeIf(s -> s.getId().equals(replaceId));
                }

                StatusBar newstatus = new StatusBar(barId, renderer == null ? defaultRenderer : renderer.update(position, direction), logic == null ? HotbarAPI.defaultLogic : logic, beforeIds, afterIds, gameModes);
                HotbarAPI.statusBars.add(newstatus);

                //HotbarAPI.statusBars.sort(StatusBar::compareTo);

                for (Iterator<StatusBar> it = HotbarAPI.statusBars.iterator(); it.hasNext();) {
                    StatusBar bar = it.next();
                    int barIndex = HotbarAPI.statusBars.indexOf(bar);
                    for (Iterator<StatusBar> it2 = HotbarAPI.statusBars.iterator(); it2.hasNext();) {
                        StatusBar bar2 = it2.next();
                        int bar2Index = HotbarAPI.statusBars.indexOf(bar2);
                        if(bar.getBeforeIds().contains(bar2.getId())) {
                            if (barIndex >= bar2Index) {
                                Collections.swap(HotbarAPI.statusBars, barIndex, bar2Index);
                                //HotbarAPI.statusBars.remove(barIndex);
                                //HotbarAPI.statusBars.add(bar2Index, bar);
                            }
                        }
                        if(bar.getAfterIds().contains(bar2.getId())) {
                            if (barIndex <= bar2Index) {
                                Collections.swap(HotbarAPI.statusBars, barIndex, bar2Index);
                                //HotbarAPI.statusBars.remove(barIndex);
                                //HotbarAPI.statusBars.add(bar2Index, bar);
                            }
                        }
                    }
                }

                /*for (StatusBar bar : HotbarAPI.statusBars) {
                    int barIndex = HotbarAPI.statusBars.indexOf(bar);
                    for (StatusBar bar2 : HotbarAPI.statusBars) {
                        int bar2Index = HotbarAPI.statusBars.indexOf(bar2);
                        if(bar.getBeforeIds().contains(bar2.getId())) {
                            if (barIndex >= bar2Index) {
                                HotbarAPI.statusBars.remove(barIndex);
                                HotbarAPI.statusBars.add(bar2Index, bar);
                            }
                        }
                    }
                }*/

                //HotbarAPI.statusBars = HotbarAPI.sortByValue(HotbarAPI.statusBars);

                //List<Map.Entry<Identifier, StatusBar>> list = new ArrayList<>(HotbarAPI.statusBars.entrySet());

                //Using Entry's comparingByValue() method for sorting in ascending order
                //list.sort(Map.Entry.comparingByValue());

                //Printing the elements from the list
                //list.forEach((fruit)->System.out.println(fruit.getKey() + " -> " + fruit.getValue()));

                //}
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
