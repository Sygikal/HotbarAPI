package dev.sygii.hotbarapi.elements.vanilla;

import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.elements.StatusBar;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import squeek.appleskin.client.HUDOverlayHandler;

public class VanillaFoodStatusBar extends StatusBar {
    private static final Identifier ICONS = new Identifier("textures/gui/icons.png");

    public VanillaFoodStatusBar() {
        super(new Identifier("hunger"), Identifier.of(HotbarAPI.MOD_ID, "textures/gui/sex.png"), Position.RIGHT, Direction.R2L);
    }

    @Override
    public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int xPosition, int yPosition) {
        HungerManager hungerManager = playerEntity.getHungerManager();
        int k = hungerManager.getFoodLevel();

        if (FabricLoader.getInstance().isModLoaded("appleskin")) {
            HUDOverlayHandler.INSTANCE.onPreRender(context);
        }

        for(int y = 0; y < 10; ++y) {
            int z = yPosition;
            int aa = 16;
            int ab = 0;
            if (playerEntity.hasStatusEffect(StatusEffects.HUNGER)) {
                aa += 36;
                ab = 13;
            }

            if (playerEntity.getHungerManager().getSaturationLevel() <= 0.0F && client.inGameHud.getTicks() % (k * 3 + 1) == 0) {
                z = yPosition + (client.inGameHud.random.nextInt(3) - 1);
            }

            int ac = xPosition - y * 8;
            context.drawTexture(ICONS, ac, z, 16 + ab * 9, 27, 9, 9);
            if (y * 2 + 1 < k) {
                context.drawTexture(ICONS, ac, z, aa + 36, 27, 9, 9);
            }

            if (y * 2 + 1 == k) {
                context.drawTexture(ICONS, ac, z, aa + 45, 27, 9, 9);
            }
        }

        if (FabricLoader.getInstance().isModLoaded("appleskin")) {
            HUDOverlayHandler.INSTANCE.onRender(context);
        }
    }
}
