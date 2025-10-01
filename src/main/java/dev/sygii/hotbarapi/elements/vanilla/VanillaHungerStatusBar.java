package dev.sygii.hotbarapi.elements.vanilla;

import dev.sygii.hotbarapi.elements.StatusBarLogic;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

//? if >1.21.1 {
/*import net.minecraft.client.render.RenderLayer;
import java.util.function.Function;
*///?}

//? if >=1.21.6 {
/*import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;
*///?}

public class VanillaHungerStatusBar {

    public static class VanillaHungerStatusBarRenderer extends StatusBarRenderer {

        //? if =1.20.1 {
        private static final Identifier ICONS = new Identifier("textures/gui/icons.png");
        public static final Identifier ID = new Identifier("hunger_renderer");
        //?} else {
        /*private static final Identifier FOOD_EMPTY_HUNGER_TEXTURE = Identifier.ofVanilla("hud/food_empty_hunger");
        private static final Identifier FOOD_HALF_HUNGER_TEXTURE = Identifier.ofVanilla("hud/food_half_hunger");
        private static final Identifier FOOD_FULL_HUNGER_TEXTURE = Identifier.ofVanilla("hud/food_full_hunger");
        private static final Identifier FOOD_EMPTY_TEXTURE = Identifier.ofVanilla("hud/food_empty");
        private static final Identifier FOOD_HALF_TEXTURE = Identifier.ofVanilla("hud/food_half");
        private static final Identifier FOOD_FULL_TEXTURE = Identifier.ofVanilla("hud/food_full");
        private static final Identifier ICONS = null;
        public static final Identifier ID = Identifier.ofVanilla("hunger_renderer");

        //? if >=1.21.6 {
        /^RenderPipeline LAYER = RenderPipelines.GUI_TEXTURED;
        ^///?} else {
        Function<Identifier, RenderLayer> LAYER = RenderLayer::getGuiTextured;
         //?}
        *///?}

        public VanillaHungerStatusBarRenderer() {
            super(ID, ICONS, StatusBarRenderer.Position.RIGHT, StatusBarRenderer.Direction.R2L);
        }

        @Override
        public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int xPosition, int yPosition, StatusBarLogic logic) {
            HungerManager hungerManager = playerEntity.getHungerManager();
            int hunger = hungerManager.getFoodLevel();

            for(int j = 0; j < 10; ++j) {
                int k = yPosition;
                Identifier identifier = null;
                Identifier identifier2 = null;
                Identifier identifier3 = null;
                //? if >=1.21.9 {
                /*if (playerEntity.hasStatusEffect(StatusEffects.HUNGER)) {
                    identifier = FOOD_EMPTY_HUNGER_TEXTURE;
                    identifier2 = FOOD_HALF_HUNGER_TEXTURE;
                    identifier3 = FOOD_FULL_HUNGER_TEXTURE;
                } else {
                    identifier = FOOD_EMPTY_TEXTURE;
                    identifier2 = FOOD_HALF_TEXTURE;
                    identifier3 = FOOD_FULL_TEXTURE;
                }
                *///?}
                int aa = 16;
                int ab = 0;
                if (playerEntity.hasStatusEffect(StatusEffects.HUNGER)) {
                    aa += 36;
                    ab = 13;
                }

                if (playerEntity.getHungerManager().getSaturationLevel() <= 0.0F && client.inGameHud.getTicks() % (hunger * 3 + 1) == 0) {
                    k = yPosition + (client.inGameHud.random.nextInt(3) - 1);
                }

                int l = xPosition + (getDirection().equals(Direction.L2R) ? (getPosition().equals(Position.RIGHT) ? -72 : 0) + j * 8 : (getPosition().equals(Position.LEFT) ? 72 : 0) + -(j * 8));

                draw(context, l, k, 16 + ab * 9, identifier);

                if (j * 2 + 1 < hunger) {
                    draw(context, l, k, aa + 36, identifier3);
                }

                if (j * 2 + 1 == hunger) {
                    draw(context, l, k, aa + 45, identifier2);
                }
            }
        }

        private void draw(DrawContext context, int x, int y, int texPos, Identifier tex) {
            //? if =1.20.1 {
            context.drawTexture(ICONS, x, y, texPos, 27, 9, 9);
            //?} else {
            /*context.drawGuiTexture(LAYER, tex, x, y, 9, 9);
            *///?}
        }
    }
}
