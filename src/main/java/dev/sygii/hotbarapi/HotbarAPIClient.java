package dev.sygii.hotbarapi;

import com.google.gson.JsonElement;
import dev.sygii.hotbarapi.access.InGameHudAccessor;
import dev.sygii.hotbarapi.access.PlayerEntityAccessor;
import dev.sygii.hotbarapi.elements.*;
import dev.sygii.hotbarapi.elements.vanilla.*;
import dev.sygii.hotbarapi.network.ClientPacketHandler;
import dev.sygii.hotbarapi.network.packet.HotbarHighlightPacket;
import dev.sygii.hotbarapi.network.packet.ResetStatusBarsS2CPacket;
import dev.sygii.hotbarapi.network.packet.StatusBarOverlayS2CPacket;
import dev.sygii.hotbarapi.network.packet.StatusBarS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
	public static final List<Identifier> replacedStatusBars = new ArrayList<Identifier>();
	public static final List<StatusBar> statusBars = new ArrayList<StatusBar>();

	public static final Map<Identifier, HotbarHighlight> hotbarHighlights = new LinkedHashMap<>();
	public static final Map<Integer, HotbarHighlight> mappedHotbarHighlights = new LinkedHashMap<>();

	@Override
	public void onInitializeClient() {
		HotbarAPI.registerStatusBarLogic(HotbarAPI.DEFAULT_STATUS_BAR_LOGIC);
		HotbarAPI.registerStatusBarRenderer(HotbarAPI.DEFAULT_STATUS_BAR_RENDERER);

		StatusBarLogic healthLogic = new StatusBarLogic(HotbarAPI.identifierOf("health_logic"), LivingEntity::getMaxHealth, LivingEntity::getHealth);
		HotbarAPI.registerStatusBarLogic(healthLogic);
		StatusBarLogic hurtLogic = new StatusBarLogic(HotbarAPI.identifierOf("hurt_time_logic"), (ent) -> ent.maxHurtTime, (ent) -> ent.hurtTime);
		HotbarAPI.registerStatusBarLogic(hurtLogic);
		StatusBarLogic staminaLogic = new StatusBarLogic(HotbarAPI.identifierOf("stamina_logic"), (ent) -> 40, (ent) -> ((PlayerEntityAccessor) ent).getStamina());
		HotbarAPI.registerStatusBarLogic(staminaLogic);

		//Air
		HotbarAPI.registerStatusBarRenderer(new VanillaAirStatusBar.VanillaAirStatusBarRenderer());
		HotbarAPI.registerStatusBarLogic(new VanillaAirStatusBar.VanillaAirStatusBarLogic());
		//Armor
		HotbarAPI.registerStatusBarRenderer(new VanillaArmorStatusBar.VanillaArmorStatusBarRenderer());
		HotbarAPI.registerStatusBarLogic(new VanillaArmorStatusBar.VanillaArmorStatusBarLogic());

		HotbarAPI.registerStatusBarRenderer(new VanillaHungerStatusBar.VanillaHungerStatusBarRenderer());

		HotbarAPI.registerStatusBarRenderer(new VanillaHealthStatusBar.VanillaHealthStatusBarRenderer());

		HotbarAPI.registerStatusBarLogic(new VanillaMountHealthStatusBar.VanillaMountHealthStatusBarLogic());
		HotbarAPI.registerStatusBarRenderer(new VanillaMountHealthStatusBar.VanillaMountHealthStatusBarRenderer());

		EnumSet<GameMode> gameModes = EnumSet.noneOf(GameMode.class);
		gameModes.add(GameMode.SURVIVAL);
		gameModes.add(GameMode.ADVENTURE);

		Identifier ARMOR = vanilla("armor");
		Identifier HEALTH = vanilla("health");
		Identifier AIR = vanilla("air");
		Identifier HUNGER = vanilla("hunger");
		Identifier MOUNT_HEALTH = vanilla("mount_health");


		List<Identifier> health_before = new ArrayList<>();
		health_before.add(ARMOR);
		HotbarAPI.registerStatusBar(new StatusBar(HEALTH,
				HotbarAPI.getRenderer(VanillaHealthStatusBar.VanillaHealthStatusBarRenderer.ID), HotbarAPI.DEFAULT_STATUS_BAR_LOGIC,
				health_before, new ArrayList<>(), HotbarAPI.NULL_STATUS_BAR_REPLACEMENT, gameModes));

		List<Identifier> hunger_before = new ArrayList<>();
		hunger_before.add(AIR);
		hunger_before.add(MOUNT_HEALTH);
		HotbarAPI.registerStatusBar(new StatusBar(HUNGER,
				HotbarAPI.getRenderer(VanillaHungerStatusBar.VanillaHungerStatusBarRenderer.ID), HotbarAPI.DEFAULT_STATUS_BAR_LOGIC,
				hunger_before, new ArrayList<>(), HotbarAPI.NULL_STATUS_BAR_REPLACEMENT, gameModes));

		List<Identifier> air_after = new ArrayList<>();
		air_after.add(HUNGER);
		air_after.add(MOUNT_HEALTH);
		HotbarAPI.registerStatusBar(new StatusBar(AIR,
				HotbarAPI.getRenderer(VanillaAirStatusBar.VanillaAirStatusBarRenderer.ID), HotbarAPI.getLogic(VanillaAirStatusBar.VanillaAirStatusBarLogic.ID),
				new ArrayList<>(), air_after, HotbarAPI.NULL_STATUS_BAR_REPLACEMENT, gameModes));

		List<Identifier> armor_after = new ArrayList<>();
		armor_after.add(HEALTH);
		HotbarAPI.registerStatusBar(new StatusBar(ARMOR,
				HotbarAPI.getRenderer(VanillaArmorStatusBar.VanillaArmorStatusBarRenderer.ID), HotbarAPI.getLogic(VanillaArmorStatusBar.VanillaArmorStatusBarLogic.ID),
				new ArrayList<>(), armor_after, HotbarAPI.NULL_STATUS_BAR_REPLACEMENT, gameModes));

		List<Identifier> mount_after = new ArrayList<>();
		List<Identifier> mount_before = new ArrayList<>();
		mount_after.add(HUNGER);
		mount_before.add(AIR);
		HotbarAPI.registerStatusBar(new StatusBar(MOUNT_HEALTH,
				HotbarAPI.getRenderer(VanillaMountHealthStatusBar.VanillaMountHealthStatusBarRenderer.ID), HotbarAPI.getLogic(VanillaMountHealthStatusBar.VanillaMountHealthStatusBarLogic.ID),
				mount_before, mount_after, HotbarAPI.NULL_STATUS_BAR_REPLACEMENT, gameModes));


		ClientPacketHandler.init();
	}

	public static Identifier vanilla(String id) {
		return Identifier.of("minecraft", id);
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