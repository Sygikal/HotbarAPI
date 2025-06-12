package dev.sygii.hotbarapi.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.elements.StatusBar;
import dev.sygii.hotbarapi.elements.StatusBarLogic;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

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
        //HotbarAPI.statusBars.clear();
        //HotbarAPI.replacedStatusBars.clear();
        HotbarAPI.serverRegisteredStatusBars.clear();
        manager.findResources("status_bar", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                InputStream stream = null;
                stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

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

                Identifier replaceId = HotbarAPI.NULL_STATUS_BAR_REPLACEMENT;
                boolean replace = false;
                if (data.has("replace")) {
                    replace = true;
                    replaceId = Identifier.tryParse(data.get("replace").getAsString());
                }
                Identifier texture = Identifier.tryParse(data.get("texture").getAsString());
                Identifier barId = Identifier.of(id.getNamespace(), statusBarId);
                StatusBarRenderer.Direction direction = StatusBarRenderer.Direction.valueOf(data.get("direction").getAsString().toUpperCase());
                StatusBarRenderer.Position position = StatusBarRenderer.Position.valueOf(data.get("position").getAsString().toUpperCase());

                StatusBarLogic logic = HotbarAPI.DEFAULT_STATUS_BAR_LOGIC;
                if (data.has("logic") && HotbarAPI.statusBarLogics.get(Identifier.tryParse(data.get("logic").getAsString())) != null) {
                    logic = HotbarAPI.statusBarLogics.get(Identifier.tryParse(data.get("logic").getAsString()));
                }

                StatusBarRenderer renderer = HotbarAPI.DEFAULT_STATUS_BAR_RENDERER;
                if (data.has("renderer") && HotbarAPI.statusBarRenderers.get(Identifier.tryParse(data.get("renderer").getAsString())) != null) {
                    renderer = HotbarAPI.statusBarRenderers.get(Identifier.tryParse(data.get("renderer").getAsString()));
                }

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

                EnumSet<GameMode> gameModes = EnumSet.noneOf(GameMode.class);
                //List<GameMode> gameModes = new ArrayList<>();
                if (data.has("gamemodes")) {
                    for (JsonElement elem : data.getAsJsonArray("gamemodes")) {
                        gameModes.add(GameMode.valueOf(elem.getAsString().toUpperCase()));
                    }
                }else {
                    gameModes.add(GameMode.SURVIVAL);
                    gameModes.add(GameMode.ADVENTURE);
                }
                if (replace) {
                    //HotbarAPI.replacedStatusBars.add(replaceId);
                    //HotbarAPI.statusBars.removeIf(s -> s.getId().equals(replaceId));
                }


                for (Identifier replaced : HotbarAPI.replacedStatusBars) {
                    //HotbarAPI.statusBars.removeIf(s -> s.getId().equals(replaced));
                }


                StatusBar newstatus = new StatusBar(barId, renderer.update(texture, position, direction), logic, beforeIds, afterIds, replaceId, gameModes);
                HotbarAPI.serverRegisteredStatusBars.add(newstatus);
                //HotbarAPI.statusBars.add(newstatus);

                //HotbarAPI.statusBars.sort(StatusBar::compareTo);

                /*for (Iterator<StatusBar> it = HotbarAPI.statusBars.iterator(); it.hasNext();) {
                    StatusBar bar = it.next();
                    int barIndex = HotbarAPI.statusBars.indexOf(bar);
                    for (Iterator<StatusBar> it2 = HotbarAPI.statusBars.iterator(); it2.hasNext();) {
                        StatusBar bar2 = it2.next();
                        int bar2Index = HotbarAPI.statusBars.indexOf(bar2);
                        if(bar.getBeforeIds().contains(bar2.getId())) {
                            if (barIndex >= bar2Index) {
                                //HotbarAPI.statusBars.set(i, HotbarAPI.statusBars.set(j, HotbarAPI.statusBars.get(i)));
                                swap(HotbarAPI.statusBars, barIndex, bar2Index);
                                //HotbarAPI.statusBars.remove(barIndex);
                                //HotbarAPI.statusBars.add(bar2Index, bar);
                            }
                        }
                        if(bar.getAfterIds().contains(bar2.getId())) {
                            if (barIndex <= bar2Index) {
                                swap(HotbarAPI.statusBars, barIndex, bar2Index);
                                //HotbarAPI.statusBars.remove(barIndex);
                                //HotbarAPI.statusBars.add(bar2Index, bar);
                            }
                        }
                    }
                }*/

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

    public static void swap(List<?> list, int i, int j) {
        // instead of using a raw type here, it's possible to capture
        // the wildcard but it will require a call to a supplementary
        // private method
        final List l = list;
        l.set(i, l.set(j, l.get(i)));
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
