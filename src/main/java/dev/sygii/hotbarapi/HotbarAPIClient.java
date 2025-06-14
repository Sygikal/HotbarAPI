package dev.sygii.hotbarapi;

import dev.sygii.hotbarapi.access.InGameHudAccessor;
import dev.sygii.hotbarapi.access.PlayerEntityAccessor;
import dev.sygii.hotbarapi.data.client.ClientStatusBarLoader;
import dev.sygii.hotbarapi.data.client.ClientStatusBarOverlayLoader;
import dev.sygii.hotbarapi.elements.*;
import dev.sygii.hotbarapi.elements.vanilla.*;
import dev.sygii.hotbarapi.network.HotbarHighlightPacket;
import dev.sygii.hotbarapi.network.ResetStatusBarsS2CPacket;
import dev.sygii.hotbarapi.network.StatusBarOverlayS2CPacket;
import dev.sygii.hotbarapi.network.StatusBarS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class HotbarAPIClient implements ClientModInitializer {
	public static final Identifier NULL_HOTBAR = Identifier.of(HotbarAPI.MOD_ID, "null_hotbar_texture");
	public static final Identifier NULL_STATUS_BAR_REPLACEMENT = Identifier.of(HotbarAPI.MOD_ID, "null_status_bar_replacement");
	public static final StatusBarRenderer DEFAULT_STATUS_BAR_RENDERER = new StatusBarRenderer(HotbarAPI.identifierOf("default_status_bar_renderer"), null, StatusBarRenderer.Position.LEFT, StatusBarRenderer.Direction.L2R);
	public static final StatusBarLogic DEFAULT_STATUS_BAR_LOGIC = new StatusBarLogic(HotbarAPI.identifierOf("default_status_bar_logic"), (ent) -> 0, (ent) -> 0);

	public static final List<Identifier> replacedStatusBars = new ArrayList<Identifier>();
	public static final List<StatusBar> statusBars = new ArrayList<StatusBar>();

	public static final Map<Identifier, HotbarHighlight> hotbarHighlights = new LinkedHashMap<>();
	public static final Map<Integer, HotbarHighlight> mappedHotbarHighlights = new LinkedHashMap<>();

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ClientStatusBarLoader());
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ClientStatusBarOverlayLoader());
		//ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new HotbarHighlightLoader());

		HotbarAPI.registerStatusBarLogic(DEFAULT_STATUS_BAR_LOGIC);
		HotbarAPI.registerStatusBarRenderer(DEFAULT_STATUS_BAR_RENDERER);

		StatusBarLogic healthLogic = new StatusBarLogic(HotbarAPI.identifierOf("health_logic"), LivingEntity::getMaxHealth, LivingEntity::getHealth);
		HotbarAPI.registerStatusBarLogic(healthLogic);
		StatusBarLogic hurtLogic = new StatusBarLogic(HotbarAPI.identifierOf("hurt_time_logic"), (ent) -> ent.maxHurtTime, (ent) -> ent.hurtTime);
		HotbarAPI.registerStatusBarLogic(hurtLogic);
		StatusBarLogic staminaLogic = new StatusBarLogic(HotbarAPI.identifierOf("stamina_logic"), (ent) -> 40, (ent) -> ((PlayerEntityAccessor) ent).getStamina());
		HotbarAPI.registerStatusBarLogic(staminaLogic);

		HotbarAPI.registerStatusBarRenderer(new VanillaAirStatusBar.VanillaAirStatusBarRenderer());
		HotbarAPI.registerStatusBarLogic(new VanillaAirStatusBar.VanillaAirStatusBarLogic());
		HotbarAPI.registerStatusBarRenderer(new VanillaArmorStatusBar.VanillaArmorStatusBarRenderer());
		HotbarAPI.registerStatusBarLogic(new VanillaArmorStatusBar.VanillaArmorStatusBarLogic());
		HotbarAPI.registerStatusBarRenderer(new VanillaHungerStatusBar.VanillaHungerStatusBarRenderer());
		HotbarAPI.registerStatusBarRenderer(new VanillaHealthStatusBar.VanillaHealthStatusBarRenderer());
		HotbarAPI.registerStatusBarLogic(new VanillaMountHealthStatusBar.VanillaMountHealthStatusBarLogic());
		HotbarAPI.registerStatusBarRenderer(new VanillaMountHealthStatusBar.VanillaMountHealthStatusBarRenderer());


		//? if =1.20.1 {
		ClientPlayNetworking.registerGlobalReceiver(HotbarHighlightPacket.PACKET_ID, (client, handler, buf, sender) -> {
			MinecraftClient clientInstance = client;
			HotbarHighlightPacket payload = new HotbarHighlightPacket(buf);
		//?} else {
		/*ClientPlayNetworking.registerGlobalReceiver(HotbarHighlightPacket.PACKET_ID, (payload, context) -> {
			 MinecraftClient clientInstance = context.client();
		*///?}
			clientInstance.execute(() -> {
				HotbarAPIClient.mappedHotbarHighlights.put(payload.slot(), HotbarAPIClient.hotbarHighlights.get(payload.highlight()));
			});
		});
		//? if =1.20.1 {
		ClientPlayNetworking.registerGlobalReceiver(ResetStatusBarsS2CPacket.PACKET_ID, (client, handler, buf, sender) -> {
			MinecraftClient clientInstance = client;
		//?} else {
		/*ClientPlayNetworking.registerGlobalReceiver(ResetStatusBarsS2CPacket.PACKET_ID, (payload, context) -> {
		    MinecraftClient clientInstance = context.client();
		*///?}
			HotbarAPIClient.statusBars.clear();
			HotbarAPIClient.replacedStatusBars.clear();
			clientInstance.execute(() -> {
				//clientInstance.getToastManager().add(SystemToast.create(clientInstance, SystemToast.Type.PACK_LOAD_FAILURE, Text.literal("Server Reload"), Text.literal("pensiia asada kasfajha jafkah leksajdi salakjadahfag leksajdahga")));
			});
		});
		//? if =1.20.1 {
		ClientPlayNetworking.registerGlobalReceiver(StatusBarS2CPacket.PACKET_ID, (client, handler, buf, sender) -> {
			MinecraftClient clientInstance = client;
			StatusBarS2CPacket payload = new StatusBarS2CPacket(buf);
		//?} else {
		/*ClientPlayNetworking.registerGlobalReceiver(StatusBarS2CPacket.PACKET_ID, (payload, context) -> {
			MinecraftClient clientInstance = context.client();
		 *///?}
			Identifier statusBarId = payload.statusBarId();
			List<Identifier> beforeIds = payload.beforeIds();
			List<Identifier> afterIds = payload.afterIds();
			Identifier toReplace = payload.toReplace();
			Identifier texture = payload.texture();
			StatusBarRenderer.Direction direction = payload.direction();
			StatusBarRenderer.Position position = payload.position();
			Identifier statusBarLogicId = payload.statusBarLogicId();
			Identifier statusBarRendererId = payload.statusBarRendererId();
			EnumSet<GameMode> gameModes = payload.gameModes();
			clientInstance.execute(() -> {
				System.out.println(statusBarId.toString() + "  |  " + beforeIds + "  |  " + afterIds+ "  |  " +toReplace+ "  |  " +texture+ "  |  " +direction+ "  |  " +position+ "  |  " +statusBarLogicId+ "  |  " +statusBarRendererId+ "  |  " +gameModes);

				StatusBarLogic logic = HotbarAPI.DEFAULT_STATUS_BAR_LOGIC;
				if (HotbarAPI.statusBarLogics.get(statusBarLogicId) != null) {
					logic = HotbarAPI.statusBarLogics.get(statusBarLogicId);
				}

				StatusBarRenderer renderer = HotbarAPI.DEFAULT_STATUS_BAR_RENDERER;
				if (HotbarAPI.statusBarRenderers.get(statusBarRendererId) != null) {
					renderer = HotbarAPI.statusBarRenderers.get(statusBarRendererId);
				}

				if (!toReplace.equals(HotbarAPIClient.NULL_STATUS_BAR_REPLACEMENT)) {
					HotbarAPIClient.replacedStatusBars.add(toReplace);
				}

				for (Identifier replaced : HotbarAPIClient.replacedStatusBars) {
					HotbarAPIClient.statusBars.removeIf(s -> s.getId().equals(replaced));
				}

				StatusBar newstatus = new StatusBar(statusBarId, renderer.update(texture, position, direction), logic, beforeIds, afterIds, toReplace, gameModes);
				HotbarAPIClient.statusBars.add(newstatus);

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
			});
		});
		//? if =1.20.1 {
		ClientPlayNetworking.registerGlobalReceiver(StatusBarOverlayS2CPacket.PACKET_ID, (client, handler, buf, sender) -> {
			StatusBarOverlayS2CPacket payload = new StatusBarOverlayS2CPacket(buf);
		//?} else {
		/*ClientPlayNetworking.registerGlobalReceiver(StatusBarOverlayS2CPacket.PACKET_ID, (payload, context) -> {
		 *///?}
			Identifier overlayBarId = payload.statusBarOverlayId();
			Identifier targetId = payload.targetId();
			Identifier texture = payload.texture();
			Identifier overlayRendererId = payload.overlayRendererId();
			Identifier overlayLogicId = payload.overlayLogicId();
			boolean underlay = payload.underlay();
			//? if =1.20.1 {
			client.execute(() -> {
			//?} else {
			/*context.client().execute(() -> {
			 *///?}
				System.out.println(overlayBarId.toString() + "  |  " + targetId.toString() + "  |  " + texture.toString()+ "  |  " +overlayRendererId.toString()+ "  |  " +overlayLogicId.toString()+ "  |  " +underlay);

				StatusBarLogic logic = HotbarAPI.DEFAULT_STATUS_BAR_LOGIC;
				if (HotbarAPI.statusBarLogics.get(overlayLogicId) != null) {
					logic = HotbarAPI.statusBarLogics.get(overlayLogicId);
				}

				StatusBarRenderer renderer = HotbarAPI.DEFAULT_STATUS_BAR_RENDERER;
				if (HotbarAPI.statusBarRenderers.get(overlayRendererId) != null) {
					renderer = HotbarAPI.statusBarRenderers.get(overlayRendererId);
				}

				StatusBar targetBar = null;
				for (StatusBar bar : HotbarAPIClient.statusBars) {
					if (bar.getId().equals(targetId)) {
						targetBar = bar;
						break;
					}
				}

				if (targetBar != null) {
					StatusBarOverlay overlay = new StatusBarOverlay(overlayBarId, targetId, renderer.update(texture, targetBar.getRenderer().getPosition(), targetBar.getRenderer().getDirection()), logic, underlay);
					if (underlay) {
						targetBar.addUnderlay(overlay);
					}else {
						targetBar.addOverlay(overlay);
					}
				}

			});
		});
	}

	private static int highlightItem(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Identifier hotbarHighlight, ItemStackArgument item) {
		for (ServerPlayerEntity serverPlayerEntity : targets) {
			int index = indexOf(serverPlayerEntity, item.getItem().getDefaultStack());
			if (index != -1) {
				ServerPlayNetworking.send(serverPlayerEntity, new HotbarHighlightPacket(index, hotbarHighlight));
				source.sendFeedback(() -> Text.literal("Highlighting item"), true);
			} else {
				source.sendFeedback(() -> Text.literal("Item not in hotbar"), true);
			}
		}

		return targets.size();
	}

	public static int indexOf(ServerPlayerEntity serverPlayerEntity, ItemStack stack) {
		for (int i = 0; i < 9; i++) {
			//? if =1.20.1 {
			ItemStack itemStack = serverPlayerEntity.getInventory().main.get(i);
			if (!serverPlayerEntity.getInventory().main.get(i).isEmpty() && itemStack.getItem() == stack.getItem()) {
			//?}
			//? if >=1.21.1 {
			/*ItemStack itemStack = serverPlayerEntity.getInventory().getMainStacks().get(i);
			if (!serverPlayerEntity.getInventory().getMainStacks().get(i).isEmpty() && itemStack.getItem() == stack.getItem()) {
			*///?}
				return i;
			}
		}

		return -1;
	}

	private static int executeSkillCommand(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Identifier hotbarHighlight, int slot) {
		for (ServerPlayerEntity serverPlayerEntity : targets) {
			ServerPlayNetworking.send(serverPlayerEntity, new HotbarHighlightPacket(slot, hotbarHighlight));
			source.sendFeedback(() -> Text.translatable("commands.level.changed", serverPlayerEntity.getDisplayName()), true);
		}

		return targets.size();
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

	public static void addStatusBar(StatusBar bar) {
		//HotbarAPI.statusBars.add(bar);
	}

	public static void addStatusBar(StatusBar bar, int index) {
		//HotbarAPI.statusBars.put(bar.getId(), bar);
		//HotbarAPI.statusBars.add(index, bar);
	}

	public static float getMaxStatusHeight(MinecraftClient client, PlayerEntity playerEntity) {
		List<StatusBar> rightBars = HotbarAPIClient.statusBars.stream().filter(s -> s.getRenderer().getPosition().equals(StatusBarRenderer.Position.RIGHT)).filter(s -> s.getLogic().isVisible(client, playerEntity)).filter(s -> s.getGameModes().contains(client.interactionManager.getCurrentGameMode())).toList();
		List<StatusBar> leftBars = HotbarAPIClient.statusBars.stream().filter(s -> s.getRenderer().getPosition().equals(StatusBarRenderer.Position.LEFT)).filter(s -> s.getLogic().isVisible(client, playerEntity)).filter(s -> s.getGameModes().contains(client.interactionManager.getCurrentGameMode())).toList();
		float rightHeight = 0;
		for (int x2 = 0; x2 < rightBars.size(); x2++) {
			rightHeight += rightBars.get(x2).getRenderer().getHeight(client, playerEntity);
		}

		float leftHeight = 0;
		for (int x2 = 0; x2 < leftBars.size(); x2++) {
			leftHeight += leftBars.get(x2).getRenderer().getHeight(client, playerEntity);
		}
		return Math.max(rightHeight, leftHeight);
	}

	public static float getHeightOffest(MinecraftClient client, StatusBar bar, PlayerEntity playerEntity) {
		List<StatusBar> filtered = HotbarAPIClient.statusBars.stream().filter(s -> s.getRenderer().getPosition().equals(bar.getRenderer().getPosition())).filter(s -> s.getLogic().isVisible(client, playerEntity)).filter(s -> s.getGameModes().contains(client.interactionManager.getCurrentGameMode())).toList();
		int size = filtered.indexOf(bar);
		float height = 0;
		for (int x2 = 0; x2 < size; x2++) {
			height += filtered.get(x2).getRenderer().getHeight(client, playerEntity);
		}
		return height;
	}
}