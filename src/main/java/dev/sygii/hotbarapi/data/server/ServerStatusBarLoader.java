package dev.sygii.hotbarapi.data.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import dev.sygii.hotbarapi.network.packet.StatusBarS2CPacket;
import dev.sygii.ultralib.data.loader.SimpleDataLoader;
import dev.sygii.ultralib.data.util.OptionalObject;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.util.*;
import java.util.List;

public class ServerStatusBarLoader extends SimpleDataLoader {
//? if <1.21.9 {
   @Override
    public Identifier getFabricId() {
        return ID;
    }
//?}
    public static final Identifier ID = HotbarAPI.identifierOf("server_status_bar_loader");

    public ServerStatusBarLoader() {
        super(ID, "status_bar");
    }

    @Override
    public void preReload() {
        HotbarAPI.statusBarPacketQueue.clear();
    }

    @Override
    public void reloadResource(JsonObject data, Identifier entryId, String fileName) {
        List<Identifier> beforeIds = new ArrayList<>();
        for (JsonElement elem : OptionalObject.get(data, "before", new JsonArray()).getAsJsonArray()) {
            beforeIds.add(Identifier.tryParse(elem.getAsString()));
        }

        List<Identifier> afterIds = new ArrayList<>();
        for (JsonElement elem : OptionalObject.get(data, "after", new JsonArray()).getAsJsonArray()) {
            afterIds.add(Identifier.tryParse(elem.getAsString()));
        }

        Identifier replaceId = Identifier.tryParse(OptionalObject.get(data, "replace", HotbarAPI.NULL_STATUS_BAR_REPLACEMENT.toString()).getAsString());
        Identifier texture = Identifier.tryParse(data.get("texture").getAsString());
        StatusBarRenderer.Direction direction = StatusBarRenderer.Direction.valueOf(data.get("direction").getAsString().toUpperCase());
        StatusBarRenderer.Position position = StatusBarRenderer.Position.valueOf(data.get("position").getAsString().toUpperCase());

        Identifier logicId = Identifier.tryParse(OptionalObject.get(data, "logic", HotbarAPI.DEFAULT_STATUS_BAR_LOGIC.getId().toString()).getAsString());
        Identifier rendererId = Identifier.tryParse(OptionalObject.get(data, "renderer", HotbarAPI.DEFAULT_STATUS_BAR_RENDERER.getId().toString()).getAsString());

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

        /*for (Identifier replaced : HotbarAPI.replacedStatusBars) {
            //HotbarAPI.statusBars.removeIf(s -> s.getId().equals(replaced));
        }*/

        //StatusBar newstatus = new StatusBar(barId, renderer.update(texture, position, direction), logic, beforeIds, afterIds, replaceId, gameModes);
        //HotbarAPI.serverRegisteredStatusBars.add(newstatus);

        HotbarAPI.statusBarPacketQueue.add(new StatusBarS2CPacket(entryId, beforeIds, afterIds, replaceId, texture, direction, position, logicId, rendererId, gameModes));
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
    }

    public static void swap(List<?> list, int i, int j) {
        // instead of using a raw type here, it's possible to capture
        // the wildcard but it will require a call to a supplementary
        // private method
        final List l = list;
        l.set(i, l.set(j, l.get(i)));
    }
}
