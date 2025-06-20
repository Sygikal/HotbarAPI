package dev.sygii.hotbarapi.elements.vanilla;

import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.elements.StatusBar;
import dev.sygii.hotbarapi.elements.StatusBarLogic;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

//? if >1.21.1
/*import net.minecraft.client.render.RenderLayer;*/

//? if >=1.21.6 {
/*import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;
*///?}

public class VanillaArmorStatusBar {

    public static class VanillaArmorStatusBarRenderer extends StatusBarRenderer {

        //? if =1.20.1 {
        private static final Identifier ICONS = new Identifier("textures/gui/icons.png");
        private static final Identifier ID = new Identifier("armor_renderer");
        //?} else {
        /*private static final Identifier ICONS = null;
        private static final Identifier ARMOR_EMPTY_TEXTURE = Identifier.ofVanilla("hud/armor_empty");
        private static final Identifier ARMOR_HALF_TEXTURE = Identifier.ofVanilla("hud/armor_half");
        private static final Identifier ARMOR_FULL_TEXTURE = Identifier.ofVanilla("hud/armor_full");
        private static final Identifier ID = Identifier.ofVanilla("armor_renderer");

        //? if >=1.21.6 {
        /^RenderPipeline LAYER = RenderPipelines.GUI_TEXTURED;
        ^///?} else {
        Function<Identifier, RenderLayer> LAYER = RenderLayer::getGuiTextured;
         //?}
        *///?}

        public VanillaArmorStatusBarRenderer() {
            super(ID, ICONS, StatusBarRenderer.Position.LEFT, StatusBarRenderer.Direction.L2R);
        }

        @Override
        public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int xPosition, int yPosition, StatusBarLogic logic) {
            //? if =1.20.1 {
            float f = Math.max((float)playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float)Math.max(client.inGameHud.renderHealthValue, MathHelper.ceil(playerEntity.getHealth())));
            int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
            int q = MathHelper.ceil((f + (float)p) / 2.0F / 10.0F);
            int r = Math.max(10 - (q - 2), 3);
            //s = o - (q - 1) * r - 10;
            int u = playerEntity.getArmor();
            for(int w = 0; w < 10; ++w) {
                if (u >= 0) {
                    int x = xPosition + (getDirection().equals(Direction.L2R) ? (getPosition().equals(Position.RIGHT) ? -72 : 0) + (w * 8) : (getPosition().equals(Position.LEFT) ? 72 : 0) + -(w * 8));
                    //int x = xPosition + w * 8;
                    if (w * 2 + 1 < u) {
                        context.drawTexture(ICONS, x, yPosition, 34, 9, 9, 9);
                    }

                    if (w * 2 + 1 == u) {
                        context.drawTexture(ICONS, x, yPosition, 25, 9, 9, 9);
                    }

                    if (w * 2 + 1 > u) {
                        context.drawTexture(ICONS, x, yPosition, 16, 9, 9, 9);
                    }
                }
            }
            //?} else {
            /*int l = playerEntity.getArmor();
            if (l > 0) {
                //int m = i - (j - 1) * k - 10;
                int m = yPosition;

                for(int n = 0; n < 10; ++n) {
                    int o = xPosition + n * 8;
                    if (n * 2 + 1 < l) {
                        context.drawGuiTexture(LAYER, ARMOR_FULL_TEXTURE, o, m, 9, 9);
                    }

                    if (n * 2 + 1 == l) {
                        context.drawGuiTexture(LAYER, ARMOR_HALF_TEXTURE, o, m, 9, 9);
                    }

                    if (n * 2 + 1 > l) {
                        context.drawGuiTexture(LAYER, ARMOR_EMPTY_TEXTURE, o, m, 9, 9);
                    }
                }

            }
            *///?}
        }
    }

    public static class VanillaArmorStatusBarLogic extends StatusBarLogic {

        //? if =1.20.1 {
        private static final Identifier ID = new Identifier("armor_logic");
         //?} else {
        /*private static final Identifier ID = Identifier.ofVanilla("armor_logic");
        *///?}

        public VanillaArmorStatusBarLogic() {
            super(ID, (ent) -> 0, (ent) -> 0);
        }

        @Override
        public boolean isVisible(MinecraftClient client, PlayerEntity playerEntity) {
            return playerEntity.getArmor() > 0;
        }
    }
}
