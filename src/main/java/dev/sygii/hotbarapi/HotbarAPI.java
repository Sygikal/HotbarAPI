package dev.sygii.hotbarapi;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.sygii.hotbarapi.access.InGameHudAccessor;
import dev.sygii.hotbarapi.data.server.HotbarHighlightLoader;
import dev.sygii.hotbarapi.data.server.ServerStatusBarLoader;
import dev.sygii.hotbarapi.data.server.ServerStatusBarOverlayLoader;
import dev.sygii.hotbarapi.elements.*;
import dev.sygii.hotbarapi.network.ServerPacketHandler;
import dev.sygii.hotbarapi.network.packet.HotbarHighlightPacket;
import dev.sygii.hotbarapi.network.packet.ResetStatusBarsS2CPacket;
import dev.sygii.hotbarapi.network.packet.StatusBarOverlayS2CPacket;
import dev.sygii.hotbarapi.network.packet.StatusBarS2CPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
//? if =1.20.1 {
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
//?} else {
/*import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
*///?}
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class HotbarAPI implements ModInitializer {
	public static final String MOD_ID = "hotbarapi";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier NULL_HOTBAR = Identifier.of(HotbarAPI.MOD_ID, "null_hotbar_texture");
	public static final Identifier NULL_STATUS_BAR_REPLACEMENT = Identifier.of(HotbarAPI.MOD_ID, "null_status_bar_replacement");
	public static final StatusBarRenderer DEFAULT_STATUS_BAR_RENDERER = new StatusBarRenderer(HotbarAPI.identifierOf("default_status_bar_renderer"), null, StatusBarRenderer.Position.LEFT, StatusBarRenderer.Direction.L2R);
	public static final StatusBarLogic DEFAULT_STATUS_BAR_LOGIC = new StatusBarLogic(HotbarAPI.identifierOf("default_status_bar_logic"), (ent) -> 0, (ent) -> 0);

	//public static final List<StatusBar> serverRegisteredStatusBars = new ArrayList<StatusBar>();
	//public static final List<StatusBarOverlay> serverRegisteredStatusBarOverlays = new ArrayList<StatusBarOverlay>();

	public static final List<StatusBarS2CPacket> statusBarPacketQueue = new ArrayList<StatusBarS2CPacket>();
	public static final List<StatusBarOverlayS2CPacket> statusBarOverlayPacketQueue = new ArrayList<StatusBarOverlayS2CPacket>();

	public static final Map<Identifier, List<StatusBarOverlay>> overlayQueue = new LinkedHashMap<>();


	//public static final List<Identifier> replacedStatusBars = new ArrayList<Identifier>();
	//public static final List<StatusBar> statusBars = new ArrayList<StatusBar>();
	public static final Map<Identifier, StatusBarLogic> statusBarLogics = new LinkedHashMap<>();
	public static final Map<Identifier, StatusBarRenderer> statusBarRenderers = new LinkedHashMap<>();

	//public static final List<HotbarHighlight> hotbarHighlights = new ArrayList<HotbarHighlight>();

	public static final Map<Identifier, HotbarHighlight> hotbarHighlights = new LinkedHashMap<>();
	public static final Map<Integer, HotbarHighlight> mappedHotbarHighlights = new LinkedHashMap<>();

	@Override
	public void onInitialize() {
		//? if >=1.21.9 {
		/*ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(ServerStatusBarLoader.ID, new ServerStatusBarLoader());
		ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(ServerStatusBarOverlayLoader.ID, new ServerStatusBarOverlayLoader());
		ResourceLoader.get(ResourceType.SERVER_DATA).addReloaderOrdering(
				ServerStatusBarLoader.ID, // Triggers first
				ServerStatusBarOverlayLoader.ID // Triggers second
		);
		*///?} else {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ServerStatusBarLoader());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new HotbarHighlightLoader());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ServerStatusBarOverlayLoader());
		//?}
		//HotbarAPI.hotbarHighlights.put(Identifier.of("hotbarapi", "test"), new HotbarHighlight(Identifier.of("hotbarapi", "test"), new Color(255, 0, 0)));

		ServerPacketHandler.init();
	}

	public static void highlightHotbarSlot(MinecraftClient client, Color color, int slot) {
		((InGameHudAccessor)(Object)(client.inGameHud)).setHighlightedSlotAndColor(slot, color);
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(Map.Entry.comparingByValue());

		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}

	public static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
		SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
				new Comparator<Map.Entry<K,V>>() {
					@Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
						if (((StatusBar)e1).getBeforeIds().contains(((StatusBar)e2).getId())) {
							return -1;
						}
						if (((StatusBar)e1).getAfterIds().contains(((StatusBar)e2).getId())) {
							return 1;
						}
						return 0;
					}
				}
		);
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	public static <T> Comparator<T> compare() {
		return (Comparator<T> & Serializable)
				(c1, c2) -> {
					System.out.println(((StatusBar)c1).getId() + "  |  " + ((StatusBar)c2).getId());
					if (((StatusBar)c1).getBeforeIds().contains(((StatusBar)c2).getId())) {
						return -1;
					}
					if (((StatusBar)c1).getAfterIds().contains(((StatusBar)c2).getId())) {
						return 1;
					}
					return 0;
				};
	}

	/*public static void replaceStatusBar(Identifier replaceId, StatusBar bar) {
		int index = -1;
		for (StatusBar sb : HotbarAPI.statusBars) {
			if (sb.getId().equals(replaceId)) {
				index = HotbarAPI.statusBars.indexOf(sb);
			}
		}
		if (index != -1) {
			HotbarAPI.statusBars.remove(index);
			HotbarAPI.statusBars.add(index, bar);
		}else {
            LOGGER.error("Status Bar `{}` does not exist!", replaceId);
		}
		//HotbarAPI.statusBars.put(bar.getId(), bar);
		//HotbarAPI.statusBarLogics.indexOf(HotbarAPI.statusBars.)
		/*HotbarAPI.statusBars = HotbarAPI.statusBars.entrySet().stream().sorted(Map.Entry.comparingByValue(comparingInt(beforeId))).collect(Collectors.toMap(
				Map.Entry::getKey,
				Map.Entry::getValue,
				(oldValue, newValue) -> oldValue, LinkedHashMap::new));//
	}

	public static void addStatusBarBefore(Identifier beforeId, StatusBar bar) {
		int index = 0;
		for (StatusBar sb : HotbarAPI.statusBars) {
			if (sb.getId().equals(beforeId)) {
				index = HotbarAPI.statusBars.indexOf(sb);
			}
		}
		HotbarAPI.statusBars.add(index, bar);
		//HotbarAPI.statusBars.put(bar.getId(), bar);
		//HotbarAPI.statusBarLogics.indexOf(HotbarAPI.statusBars.)
		/*HotbarAPI.statusBars = HotbarAPI.statusBars.entrySet().stream().sorted(Map.Entry.comparingByValue(comparingInt(beforeId))).collect(Collectors.toMap(
				Map.Entry::getKey,
				Map.Entry::getValue,
				(oldValue, newValue) -> oldValue, LinkedHashMap::new));//
	}*/

	public static StatusBarLogic getLogic(Identifier id) {
		StatusBarLogic logic = HotbarAPI.DEFAULT_STATUS_BAR_LOGIC;
		if (HotbarAPI.statusBarLogics.get(id) != null) {
			logic = HotbarAPI.statusBarLogics.get(id);
		}
		return logic;
	}

	public static StatusBarRenderer getRenderer(Identifier id) {
		StatusBarRenderer renderer = HotbarAPI.DEFAULT_STATUS_BAR_RENDERER;
		if (HotbarAPI.statusBarRenderers.get(id) != null) {
			renderer = HotbarAPI.statusBarRenderers.get(id);
		}
		return renderer;
	}

	public static void addToReplaced(Identifier id) {
		if (!id.equals(HotbarAPI.NULL_STATUS_BAR_REPLACEMENT)) {
			HotbarAPIClient.replacedStatusBars.add(id);
		}
	}

	public static void removeReplaced() {
		for (Identifier replaced : HotbarAPIClient.replacedStatusBars) {
			HotbarAPIClient.statusBars.removeIf(s -> s.getId().equals(replaced));
		}
	}

	public static StatusBar getTargetBar(Identifier id) {
		StatusBar targetBar = null;
		for (StatusBar bar : HotbarAPIClient.statusBars) {
			if (bar.getId().equals(id)) {
				targetBar = bar;
				break;
			}
		}
		return targetBar;
	}

	public static void registerStatusBarOverlay(Identifier targetId, StatusBarOverlay overlay) {
		StatusBar targetBar = HotbarAPI.getTargetBar(targetId);
		if (targetBar == null) {
			overlayQueue.computeIfAbsent(targetId, k -> new ArrayList<>()).add(overlay);
			LOGGER.info("Target was absent, adding {} to queue", overlay.getId());
		} else {
			overlay.update(targetBar);
			if (overlay.isUnderlay()) {
				targetBar.addUnderlay(overlay);
			}else {
				targetBar.addOverlay(overlay);
			}
			LOGGER.info("Registering Status Bar {}: {} | {} | {} | {} | {}", overlay.isUnderlay() ? "Underlay" : "Overlay", overlay.getId(), targetId, overlay.getRenderer().getTexture(), overlay.getRenderer().getId(), overlay.getLogic().getId());
		}
	}

	public static void registerStatusBar(StatusBar bar) {
		addToReplaced(bar.getReplacing());
		removeReplaced();
		HotbarAPIClient.statusBars.add(bar);
		sortStatusBars();

		if (overlayQueue.containsKey(bar.getId())) {
			for (StatusBarOverlay statusBarOverlay : overlayQueue.get(bar.getId())) {
				registerStatusBarOverlay(bar.getId(), statusBarOverlay);
			}
		}

		String befores = "[" +
				bar.getBeforeIds()
						.stream()
						.map(Identifier::toString)
						.collect(Collectors.joining(", ")) + "]";

		String afters = "[" +
				bar.getAfterIds()
						.stream()
						.map(Identifier::toString)
						.collect(Collectors.joining(", ")) + "]";

		String modes = "[" +
				bar.getGameModes()
						.stream()
						.map(GameMode::asString)
						.collect(Collectors.joining(", ")) + "]";
        LOGGER.info("Registering Status Bar: {} | {} | {} | {} | {} | {} | {} | {} | {} | {}", bar.getId(), befores, afters, bar.getReplacing(), bar.getRenderer().getTexture(), bar.getRenderer().getDirection(), bar.getRenderer().getPosition(), bar.getLogic().getId(), bar.getRenderer().getId(), modes);
	}

	public static void sortStatusBars() {
		for (Iterator<StatusBar> it = HotbarAPIClient.statusBars.iterator(); it.hasNext();) {
			StatusBar bar = it.next();
			int barIndex = HotbarAPIClient.statusBars.indexOf(bar);
			for (Iterator<StatusBar> it2 = HotbarAPIClient.statusBars.iterator(); it2.hasNext();) {
				StatusBar bar2 = it2.next();
				int bar2Index = HotbarAPIClient.statusBars.indexOf(bar2);
				if(bar.getBeforeIds().contains(bar2.getId())) {
					if (barIndex >= bar2Index) {
						//HotbarAPI.statusBars.set(i, HotbarAPI.statusBars.set(j, HotbarAPI.statusBars.get(i)));
						Collections.swap(HotbarAPIClient.statusBars, barIndex, bar2Index);
					}
				}
				if(bar.getAfterIds().contains(bar2.getId())) {
					if (barIndex <= bar2Index) {
						Collections.swap(HotbarAPIClient.statusBars, barIndex, bar2Index);
					}
				}
			}
		}
	}

	public static void registerStatusBarLogic(StatusBarLogic logic) {
		HotbarAPI.statusBarLogics.put(logic.getId(), logic);
	}

	public static void registerStatusBarRenderer(StatusBarRenderer renderer) {
		HotbarAPI.statusBarRenderers.put(renderer.getId(), renderer);
	}

	public static Identifier identifierOf(String name) {
		return Identifier.of(MOD_ID, name);
	}
}