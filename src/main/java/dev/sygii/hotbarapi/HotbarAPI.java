package dev.sygii.hotbarapi;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.sygii.hotbarapi.access.InGameHudAccessor;
import dev.sygii.hotbarapi.data.server.HotbarHighlightLoader;
import dev.sygii.hotbarapi.data.server.ServerStatusBarLoader;
import dev.sygii.hotbarapi.data.server.ServerStatusBarOverlayLoader;
import dev.sygii.hotbarapi.elements.*;
import dev.sygii.hotbarapi.network.HotbarHighlightPacket;
import dev.sygii.hotbarapi.network.ResetStatusBarsS2CPacket;
import dev.sygii.hotbarapi.network.StatusBarOverlayS2CPacket;
import dev.sygii.hotbarapi.network.StatusBarS2CPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
//? if >1.20.1
/*import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;*/
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;
//? if =1.20.1
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class HotbarAPI implements ModInitializer {
	public static final String MOD_ID = "hotbarapi";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier NULL_HOTBAR = Identifier.of(HotbarAPI.MOD_ID, "null_hotbar_texture");
	public static final Identifier NULL_STATUS_BAR_REPLACEMENT = Identifier.of(HotbarAPI.MOD_ID, "null_status_bar_replacement");
	public static final StatusBarRenderer DEFAULT_STATUS_BAR_RENDERER = new StatusBarRenderer(HotbarAPI.identifierOf("default_status_bar_renderer"), null, StatusBarRenderer.Position.LEFT, StatusBarRenderer.Direction.L2R);
	public static final StatusBarLogic DEFAULT_STATUS_BAR_LOGIC = new StatusBarLogic(HotbarAPI.identifierOf("default_status_bar_logic"), (ent) -> 0, (ent) -> 0);



	//public static Map<Identifier, StatusBar> statusBars = new LinkedHashMap<>();

	//public static Map<Identifier, StatusBar> statusBars = new TreeMap<>();

	/*SortedMap<String, Identifier> tm
			= new TreeMap<String, Identifier>((a, b) -> b.compareTo(a));*/

	//public static final List<StatusBar> serverRegisteredStatusBars = new ArrayList<StatusBar>();
	//public static final List<StatusBarOverlay> serverRegisteredStatusBarOverlays = new ArrayList<StatusBarOverlay>();

	public static final List<StatusBarS2CPacket> statusBarPacketQueue = new ArrayList<StatusBarS2CPacket>();
	public static final List<StatusBarOverlayS2CPacket> statusBarOverlayPacketQueue = new ArrayList<StatusBarOverlayS2CPacket>();


	//public static final List<Identifier> replacedStatusBars = new ArrayList<Identifier>();
	//public static final List<StatusBar> statusBars = new ArrayList<StatusBar>();
	public static final Map<Identifier, StatusBarLogic> statusBarLogics = new LinkedHashMap<>();
	public static final Map<Identifier, StatusBarRenderer> statusBarRenderers = new LinkedHashMap<>();

	//public static final List<HotbarHighlight> hotbarHighlights = new ArrayList<HotbarHighlight>();

	public static final Map<Identifier, HotbarHighlight> hotbarHighlights = new LinkedHashMap<>();
	public static final Map<Integer, HotbarHighlight> mappedHotbarHighlights = new LinkedHashMap<>();

	private static final SuggestionProvider<ServerCommandSource> HIGHLIGHT_SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestIdentifiers(
			HotbarAPI.hotbarHighlights.values().stream().map(HotbarHighlight::getId), builder);

	@Override
	public void onInitialize() {
		//ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new StatusBarLoader());
		//ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new HotbarHighlightLoader());
		//ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new StatusBarOverlayLoader());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ServerStatusBarLoader());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new HotbarHighlightLoader());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ServerStatusBarOverlayLoader());

		//HotbarAPI.hotbarHighlights.put(Identifier.of("hotbarapi", "test"), new HotbarHighlight(Identifier.of("hotbarapi", "test"), new Color(255, 0, 0)));

		/*registerStatusBarLogic(DEFAULT_STATUS_BAR_LOGIC);
		registerStatusBarRenderer(DEFAULT_STATUS_BAR_RENDERER);

		StatusBarLogic healthLogic = new StatusBarLogic(HotbarAPI.identifierOf("health_logic"), LivingEntity::getMaxHealth, LivingEntity::getHealth);
		registerStatusBarLogic(healthLogic);
		StatusBarLogic hurtLogic = new StatusBarLogic(HotbarAPI.identifierOf("hurt_time_logic"), (ent) -> ent.maxHurtTime, (ent) -> ent.hurtTime);
		registerStatusBarLogic(hurtLogic);
		StatusBarLogic staminaLogic = new StatusBarLogic(HotbarAPI.identifierOf("stamina_logic"), (ent) -> 40, (ent) -> ((PlayerEntityAccessor) ent).getStamina());
		registerStatusBarLogic(staminaLogic);

		registerStatusBarRenderer(new VanillaAirStatusBar.VanillaAirStatusBarRenderer());
		registerStatusBarLogic(new VanillaAirStatusBar.VanillaAirStatusBarLogic());
		registerStatusBarRenderer(new VanillaArmorStatusBar.VanillaArmorStatusBarRenderer());
		registerStatusBarLogic(new VanillaArmorStatusBar.VanillaArmorStatusBarLogic());
		registerStatusBarRenderer(new VanillaHungerStatusBar.VanillaHungerStatusBarRenderer());
		registerStatusBarRenderer(new VanillaHealthStatusBar.VanillaHealthStatusBarRenderer());
		registerStatusBarLogic(new VanillaMountHealthStatusBar.VanillaMountHealthStatusBarLogic());
		registerStatusBarRenderer(new VanillaMountHealthStatusBar.VanillaMountHealthStatusBarRenderer());*/

		/*try (InputStream in = getClass().getResourceAsStream("/data/status_bar/");
			 JarInputStream jar = new JarInputStream(getClass().getResourceAsStream());
			 BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			 System.out.println(reader.lines());
		} catch (IOException e) {
            throw new RuntimeException(e);
        }*/

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
			dispatcher.register((CommandManager.literal("hhs").requires((serverCommandSource) -> {
				return serverCommandSource.hasPermissionLevel(2);
			})).then(CommandManager.argument("targets", EntityArgumentType.players())
					// Add values
					.then(CommandManager.literal("add").then(CommandManager.argument("hotbarHighlight", IdentifierArgumentType.identifier()).suggests(HIGHLIGHT_SUGGESTION_PROVIDER).then(CommandManager.argument("slot", IntegerArgumentType.integer(0, 8)).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IdentifierArgumentType.getIdentifier(commandContext, "hotbarHighlight"),
								IntegerArgumentType.getInteger(commandContext, "slot"));
					}))))
					.then(CommandManager.literal("item").then(CommandManager.argument("itemName", ItemStackArgumentType.itemStack(dedicated)).then(CommandManager.argument("hotbarHighlight", IdentifierArgumentType.identifier()).suggests(HIGHLIGHT_SUGGESTION_PROVIDER).executes((commandContext) -> {
						return highlightItem(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IdentifierArgumentType.getIdentifier(commandContext, "hotbarHighlight"),
								ItemStackArgumentType.getItemStackArgument(commandContext, "itemName"));
					}))))));
		});
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(HotbarAPI.identifierOf("sync_status_bars"), (player, joined) -> {
			ServerPlayNetworking.send(player, new ResetStatusBarsS2CPacket());
			/*for (StatusBar statusBar : HotbarAPI.serverRegisteredStatusBars) {
				ServerPlayNetworking.send(player, new StatusBarS2CPacket(statusBar.getId(), statusBar.getBeforeIds(), statusBar.getAfterIds(), statusBar.getReplacing(), statusBar.getRenderer().getTexture(), statusBar.getRenderer().getDirection(), statusBar.getRenderer().getPosition(), statusBar.getLogic().getId(), statusBar.getRenderer().getId(), statusBar.getGameModes()));
			}
			for (StatusBarOverlay overlay : HotbarAPI.serverRegisteredStatusBarOverlays) {
				ServerPlayNetworking.send(player, new StatusBarOverlayS2CPacket(overlay.getId(), overlay.getTarget(), overlay.getRenderer().getTexture(), overlay.getLogic().getId(), overlay.getRenderer().getId(), overlay.isUnderlay()));
			}*/
			HotbarAPI.statusBarPacketQueue.forEach((packet) -> ServerPlayNetworking.send(player, packet));
			HotbarAPI.statusBarOverlayPacketQueue.forEach((packet) -> ServerPlayNetworking.send(player, packet));
		});
		//? if >1.20.1 {
		/*PayloadTypeRegistry.playS2C().register(HotbarHighlightPacket.PACKET_ID, HotbarHighlightPacket.PACKET_CODEC);
		PayloadTypeRegistry.playS2C().register(ResetStatusBarsS2CPacket.PACKET_ID, ResetStatusBarsS2CPacket.PACKET_CODEC);
		PayloadTypeRegistry.playS2C().register(StatusBarS2CPacket.PACKET_ID, StatusBarS2CPacket.PACKET_CODEC);
		PayloadTypeRegistry.playS2C().register(StatusBarOverlayS2CPacket.PACKET_ID, StatusBarOverlayS2CPacket.PACKET_CODEC);
	    *///?}
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