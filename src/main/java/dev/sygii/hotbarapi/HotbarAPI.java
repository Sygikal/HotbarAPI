package dev.sygii.hotbarapi;

import dev.sygii.hotbarapi.vanilla.*;
import net.fabricmc.api.ModInitializer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class HotbarAPI implements ModInitializer {
	public static final String MOD_ID = "hotbarapi";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final List<StatusBar> statusBars = new ArrayList<StatusBar>();

	@Override
	public void onInitialize() {
		addStatusBar(new VanillaHealthStatusBar());
		addStatusBar(new VanillaArmorStatusBar());

		addStatusBar(new VanillaFoodStatusBar());
		addStatusBar(new VanillaMountHealthStatusBar());
		addStatusBar(new VanillaAirStatusBar());


		addStatusBar(new StatusBar(Identifier.of(MOD_ID, "test"), Identifier.of(HotbarAPI.MOD_ID, "textures/gui/custom_heart.png"), StatusBar.Position.LEFT, StatusBar.Direction.L2R));
		addStatusBar(new StatusBar(Identifier.of(MOD_ID, "stamina"), Identifier.of(HotbarAPI.MOD_ID, "textures/gui/stamina.png"), StatusBar.Position.LEFT, StatusBar.Direction.R2L));
		addStatusBar(new StatusBar(Identifier.of(MOD_ID, "sex"), Identifier.of(HotbarAPI.MOD_ID, "textures/gui/sex.png"), StatusBar.Position.LEFT, StatusBar.Direction.L2R));
		addStatusBar(new StatusBar(Identifier.of(MOD_ID, "thirst"), Identifier.of(HotbarAPI.MOD_ID, "textures/gui/thirst.png"), StatusBar.Position.RIGHT, StatusBar.Direction.R2L));

	}

	public static void addStatusBar(StatusBar bar) {
		HotbarAPI.statusBars.add(bar);
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
}