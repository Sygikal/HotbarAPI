package dev.sygii.hotbarapi;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.sygii.hotbarapi.access.InGameHudAccessor;
import dev.sygii.hotbarapi.access.PlayerEntityAccessor;
import dev.sygii.hotbarapi.data.HotbarHighlightLoader;
import dev.sygii.hotbarapi.elements.HotbarHighlight;
import dev.sygii.hotbarapi.elements.SimpleStatusBar;
import dev.sygii.hotbarapi.elements.StatusBar;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.core.jmx.Server;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.*;
import java.util.List;

public class HotbarAPI implements ModInitializer {
	public static final String MOD_ID = "hotbarapi";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier NULL_HOTBAR = Identifier.of(HotbarAPI.MOD_ID, "null_hotbar_texture");

	public static final List<StatusBar> statusBars = new ArrayList<StatusBar>();
	//public static final List<HotbarHighlight> hotbarHighlights = new ArrayList<HotbarHighlight>();

	public static final Map<Identifier, HotbarHighlight> hotbarHighlights = new LinkedHashMap<>();
	public static final Map<Integer, HotbarHighlight> mappedHotbarHighlights = new LinkedHashMap<>();
	public static KeyBinding screenKey = new KeyBinding("key.hotbarapi.test", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.hotbarapi.keybind");

	private static final SuggestionProvider<ServerCommandSource> HIGHLIGHT_SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestIdentifiers(
			HotbarAPI.hotbarHighlights.values().stream().map(HotbarHighlight::getId), builder);

	@Override
	public void onInitialize() {

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new HotbarHighlightLoader());
		addStatusBar(new VanillaHealthStatusBar());
		//addStatusBar(new SimpleStatusBar(Identifier.of(MOD_ID, "full_health"), Identifier.of(HotbarAPI.MOD_ID, "textures/gui/custom_heart.png"), StatusBar.Position.LEFT, StatusBar.Direction.L2R, (playerEntity) -> playerEntity.getMaxHealth(), (ent) -> ent.getHealth()));
		addStatusBar(new VanillaArmorStatusBar());

		addStatusBar(new VanillaFoodStatusBar());
		addStatusBar(new VanillaMountHealthStatusBar());
		addStatusBar(new VanillaAirStatusBar());

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
		addStatusBar(new SimpleStatusBar(HotbarAPI.identifierOf("stamina"), HotbarAPI.identifierOf( "textures/gui/stamina.png"), StatusBar.Position.RIGHT, StatusBar.Direction.R2L, (playerEntity) -> 40, (ent) -> ((PlayerEntityAccessor)ent).getStamina()));


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

	public static void addStatusBar(StatusBar bar) {
		HotbarAPI.statusBars.add(bar);
	}

	public static float getMaxStatusHeight(MinecraftClient client, PlayerEntity playerEntity) {
		List<StatusBar> rightBars = HotbarAPI.statusBars.stream().filter(s -> s.getPosition().equals(StatusBar.Position.RIGHT)).filter(s -> s.isVisible(client, playerEntity)).toList();
		List<StatusBar> leftBars = HotbarAPI.statusBars.stream().filter(s -> s.getPosition().equals(StatusBar.Position.LEFT)).filter(s -> s.isVisible(client, playerEntity)).toList();
		float rightHeight = 0;
		for (int x2 = 0; x2 < rightBars.size(); x2++) {
			rightHeight += rightBars.get(x2).getHeight(client, playerEntity);
		}

		float leftHeight = 0;
		for (int x2 = 0; x2 < leftBars.size(); x2++) {
			leftHeight += leftBars.get(x2).getHeight(client, playerEntity);
		}
		return Math.max(rightHeight, leftHeight);
	}

	public static float getHeightOffest(MinecraftClient client, StatusBar bar, PlayerEntity playerEntity) {
		List<StatusBar> filtered = HotbarAPI.statusBars.stream().filter(s -> s.getPosition().equals(bar.getPosition())).filter(s -> s.isVisible(client, playerEntity)).toList();
		int size = filtered.indexOf(bar);
		float height = 0;
		for (int x2 = 0; x2 < size; x2++) {
			height += filtered.get(x2).getHeight(client, playerEntity);
		}
		return height;
	}

	public static Identifier identifierOf(String name) {
		return Identifier.of(MOD_ID, name);
	}
}