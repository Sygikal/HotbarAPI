package dev.sygii.hotbarapi;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.sygii.hotbarapi.access.InGameHudAccessor;
import dev.sygii.hotbarapi.access.PlayerEntityAccessor;
import dev.sygii.hotbarapi.data.HotbarHighlightLoader;
import dev.sygii.hotbarapi.data.StatusBarLoader;
import dev.sygii.hotbarapi.elements.HotbarHighlight;
import dev.sygii.hotbarapi.elements.StatusBar;
import dev.sygii.hotbarapi.elements.StatusBarLogic;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import dev.sygii.hotbarapi.network.HotbarHighlightPacket;
import dev.sygii.hotbarapi.elements.vanilla.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class HotbarAPI implements ModInitializer {
	public static final String MOD_ID = "hotbarapi";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier NULL_HOTBAR = Identifier.of(HotbarAPI.MOD_ID, "null_hotbar_texture");

	//public static Map<Identifier, StatusBar> statusBars = new LinkedHashMap<>();

	//public static Map<Identifier, StatusBar> statusBars = new TreeMap<>();

	/*SortedMap<String, Identifier> tm
			= new TreeMap<String, Identifier>((a, b) -> b.compareTo(a));*/

	public static final List<StatusBar> statusBars = new ArrayList<StatusBar>();
	public static final Map<Identifier, StatusBarLogic> statusBarLogics = new LinkedHashMap<>();
	public static final Map<Identifier, StatusBarRenderer> statusBarRenderers = new LinkedHashMap<>();

	//public static final List<HotbarHighlight> hotbarHighlights = new ArrayList<HotbarHighlight>();

	public static final Map<Identifier, HotbarHighlight> hotbarHighlights = new LinkedHashMap<>();
	public static final Map<Integer, HotbarHighlight> mappedHotbarHighlights = new LinkedHashMap<>();
	public static KeyBinding screenKey = new KeyBinding("key.hotbarapi.test", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.hotbarapi.keybind");

	private static final SuggestionProvider<ServerCommandSource> HIGHLIGHT_SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestIdentifiers(
			HotbarAPI.hotbarHighlights.values().stream().map(HotbarHighlight::getId), builder);

	public static StatusBarLogic defaultLogic = new StatusBarLogic(HotbarAPI.identifierOf("default_logic"), (ent) -> 0, (ent) -> 0);

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new StatusBarLoader());

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new HotbarHighlightLoader());
		//addStatusBar(new VanillaHealthStatusBar());
		//addStatusBar(new SimpleStatusBar(Identifier.of(MOD_ID, "full_health"), Identifier.of(HotbarAPI.MOD_ID, "textures/gui/custom_heart.png"), StatusBar.Position.LEFT, StatusBar.Direction.L2R, (playerEntity) -> playerEntity.getMaxHealth(), (ent) -> ent.getHealth()));
		//addStatusBar(new VanillaArmorStatusBar());

		//addStatusBar(new VanillaFoodStatusBar());
		//addStatusBar(new VanillaMountHealthStatusBar());
		//addStatusBar(new VanillaAirStatusBar());

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (screenKey.wasPressed()) {
				int min = 0;
				int max = 8;
				int randomNumber = (int) (Math.random() * (max - min + 1)) + min;
				//HotbarAPI.highlightHotbarSlot(client, new Color(255, 0,0), randomNumber);
				//HotbarAPI.hotbarHighlights.add(new HotbarHighlight(Identifier.of("hotbarapi", "test" + randomNumber), randomNumber, new Color(255, 0, 0)));
				if(HotbarAPI.mappedHotbarHighlights.containsKey(randomNumber)) {
					HotbarAPI.mappedHotbarHighlights.remove(randomNumber);
				}
				HotbarAPI.mappedHotbarHighlights.put(randomNumber, new HotbarHighlight(HotbarAPI.identifierOf( "test" + randomNumber), new Color(255, 0, 0)));
				return;
			}
		});

		HotbarAPI.hotbarHighlights.put(Identifier.of("hotbarapi", "test"), new HotbarHighlight(Identifier.of("hotbarapi", "test"), new Color(255, 0, 0)));

		//addStatusBar(new StatusBar(Identifier.of(MOD_ID, "stamina"), Identifier.of(HotbarAPI.MOD_ID, "textures/gui/stamina.png"), StatusBar.Position.LEFT, StatusBar.Direction.R2L));
		//addStatusBar(new StatusBar(Identifier.of(MOD_ID, "sex"), Identifier.of(HotbarAPI.MOD_ID, "textures/gui/sex.png"), StatusBar.Position.LEFT, StatusBar.Direction.L2R));
		//addStatusBar(new StatusBar(Identifier.of(MOD_ID, "thirst"), Identifier.of(HotbarAPI.MOD_ID, "textures/gui/thirst.png"), StatusBar.Position.RIGHT, StatusBar.Direction.R2L));
		//addStatusBar(new StatusBar(HotbarAPI.identifierOf("stamina"), HotbarAPI.identifierOf( "textures/gui/stamina.png"), StatusBar.Position.RIGHT, StatusBar.Direction.R2L, staminaLogic));

		StatusBarLogic staminaLogic = new StatusBarLogic(HotbarAPI.identifierOf("stamina_logic"), (playerEntity) -> 40, (ent) -> ((PlayerEntityAccessor)ent).getStamina());
		registerStatusBarLogic(staminaLogic);

		StatusBarLogic healthLogic = new StatusBarLogic(HotbarAPI.identifierOf("health_logic"), LivingEntity::getMaxHealth, LivingEntity::getHealth);
		registerStatusBarLogic(healthLogic);

		registerStatusBarRenderer(new VanillaAirStatusBar.VanillaAirStatusBarRenderer());
		registerStatusBarLogic(new VanillaAirStatusBar.VanillaAirStatusBarLogic());
		registerStatusBarRenderer(new VanillaArmorStatusBar.VanillaArmorStatusBarRenderer());
		registerStatusBarLogic(new VanillaArmorStatusBar.VanillaArmorStatusBarLogic());
		registerStatusBarRenderer(new VanillaFoodStatusBar.VanillaFoodStatusBarRenderer());
		registerStatusBarRenderer(new VanillaHealthStatusBar.VanillaHealthStatusBarRenderer());
		registerStatusBarLogic(new VanillaMountHealthStatusBar.VanillaMountHealthStatusBarLogic());
		registerStatusBarRenderer(new VanillaMountHealthStatusBar.VanillaMountHealthStatusBarRenderer());



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

		ClientPlayNetworking.registerGlobalReceiver(HotbarHighlightPacket.PACKET_ID, (client, handler, buf, sender) -> {
			HotbarHighlightPacket payload = new HotbarHighlightPacket(buf);
			int slot = payload.slot();
			Identifier highlight = payload.highlight();
			client.execute(() -> {
				HotbarAPI.mappedHotbarHighlights.put(slot, HotbarAPI.hotbarHighlights.get(highlight));
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
			ItemStack itemStack = serverPlayerEntity.getInventory().main.get(i);
			if (!serverPlayerEntity.getInventory().main.get(i).isEmpty() && ItemStack.areEqual(itemStack, stack)) {
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

	public static float getMaxStatusHeight(MinecraftClient client, PlayerEntity playerEntity) {
		List<StatusBar> rightBars = HotbarAPI.statusBars.stream().filter(s -> s.getRenderer().getPosition().equals(StatusBarRenderer.Position.RIGHT)).filter(s -> s.getLogic().isVisible(client, playerEntity)).toList();
		List<StatusBar> leftBars = HotbarAPI.statusBars.stream().filter(s -> s.getRenderer().getPosition().equals(StatusBarRenderer.Position.LEFT)).filter(s -> s.getLogic().isVisible(client, playerEntity)).toList();
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
		List<StatusBar> filtered = HotbarAPI.statusBars.stream().filter(s -> s.getRenderer().getPosition().equals(bar.getRenderer().getPosition())).filter(s -> s.getLogic().isVisible(client, playerEntity)).filter(s -> s.getGameModes().contains(client.interactionManager.getCurrentGameMode())).toList();
		int size = filtered.indexOf(bar);
		float height = 0;
		for (int x2 = 0; x2 < size; x2++) {
			height += filtered.get(x2).getRenderer().getHeight(client, playerEntity);
		}
		return height;
	}

	public static Identifier identifierOf(String name) {
		return Identifier.of(MOD_ID, name);
	}
}