package dev.sygii.hotbarapi.elements.vanilla;

import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.elements.StatusBar;
import dev.sygii.hotbarapi.elements.StatusBarLogic;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import squeek.appleskin.client.HUDOverlayHandler;

public class VanillaFoodStatusBar {

    public static class VanillaFoodStatusBarRenderer extends StatusBarRenderer {
        private static final Identifier FOOD_EMPTY_HUNGER_TEXTURE = Identifier.ofVanilla("hud/food_empty_hunger");
        private static final Identifier FOOD_HALF_HUNGER_TEXTURE = Identifier.ofVanilla("hud/food_half_hunger");
        private static final Identifier FOOD_FULL_HUNGER_TEXTURE = Identifier.ofVanilla("hud/food_full_hunger");
        private static final Identifier FOOD_EMPTY_TEXTURE = Identifier.ofVanilla("hud/food_empty");
        private static final Identifier FOOD_HALF_TEXTURE = Identifier.ofVanilla("hud/food_half");
        private static final Identifier FOOD_FULL_TEXTURE = Identifier.ofVanilla("hud/food_full");

        public VanillaFoodStatusBarRenderer() {
            super(Identifier.ofVanilla("food_renderer"), null, StatusBarRenderer.Position.RIGHT, StatusBarRenderer.Direction.R2L);
        }

        @Override
        public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int xPosition, int yPosition, StatusBarLogic logic) {
            /*HungerManager hungerManager = playerEntity.getHungerManager();
            int k = hungerManager.getFoodLevel();*/

            /*if (FabricLoader.getInstance().isModLoaded("appleskin")) {
                HUDOverlayHandler.INSTANCE.onPreRender(context);
            }*/

            /*for(int y = 0; y < 10; ++y) {
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

                //int ac = xPosition - y * 8;
                int ac = xPosition + (getDirection().equals(Direction.L2R) ? (getPosition().equals(Position.RIGHT) ? -72 : 0) + y * 8 : (getPosition().equals(Position.LEFT) ? 72 : 0) + -(y * 8));

                context.drawTexture(ICONS, ac, z, 16 + ab * 9, 27, 9, 9);
                if (y * 2 + 1 < k) {
                    context.drawTexture(ICONS, ac, z, aa + 36, 27, 9, 9);
                }

                if (y * 2 + 1 == k) {
                    context.drawTexture(ICONS, ac, z, aa + 45, 27, 9, 9);
                }
            }*/

            /*if (FabricLoader.getInstance().isModLoaded("appleskin")) {
                HUDOverlayHandler.INSTANCE.onRender(context);
            }*/

            HungerManager hungerManager = playerEntity.getHungerManager();
            int i = hungerManager.getFoodLevel();

            for(int j = 0; j < 10; ++j) {
                int k = yPosition;
                Identifier identifier;
                Identifier identifier2;
                Identifier identifier3;
                if (playerEntity.hasStatusEffect(StatusEffects.HUNGER)) {
                    identifier = FOOD_EMPTY_HUNGER_TEXTURE;
                    identifier2 = FOOD_HALF_HUNGER_TEXTURE;
                    identifier3 = FOOD_FULL_HUNGER_TEXTURE;
                } else {
                    identifier = FOOD_EMPTY_TEXTURE;
                    identifier2 = FOOD_HALF_TEXTURE;
                    identifier3 = FOOD_FULL_TEXTURE;
                }

                if (playerEntity.getHungerManager().getSaturationLevel() <= 0.0F && client.inGameHud.getTicks() % (i * 3 + 1) == 0) {
                    k = yPosition + (client.inGameHud.random.nextInt(3) - 1);
                }

                //int l = xPosition - j * 8 - 9;
                int l = xPosition + (getDirection().equals(Direction.L2R) ? (getPosition().equals(Position.RIGHT) ? -72 : 0) + j * 8 : (getPosition().equals(Position.LEFT) ? 72 : 0) + -(j * 8));

                context.drawGuiTexture(RenderLayer::getGuiTextured, identifier, l, k, 9, 9);
                if (j * 2 + 1 < i) {
                    context.drawGuiTexture(RenderLayer::getGuiTextured, identifier3, l, k, 9, 9);
                }

                if (j * 2 + 1 == i) {
                    context.drawGuiTexture(RenderLayer::getGuiTextured, identifier2, l, k, 9, 9);
                }
            }
        }
    }
}
