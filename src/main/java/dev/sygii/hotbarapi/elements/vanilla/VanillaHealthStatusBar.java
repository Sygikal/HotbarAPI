package dev.sygii.hotbarapi.elements.vanilla;

import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.elements.StatusBar;
import dev.sygii.hotbarapi.elements.StatusBarLogic;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
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

public class VanillaHealthStatusBar {
    public static class VanillaHealthStatusBarRenderer extends StatusBarRenderer {

        //? if =1.20.1 {
        private static final Identifier ICONS = new Identifier("textures/gui/icons.png");
        private static final Identifier ID = new Identifier("health_renderer");
        //?} else {
        /*private static final Identifier ICONS = null;
        private static final Identifier VEHICLE_CONTAINER_HEART_TEXTURE = Identifier.ofVanilla("hud/heart/vehicle_container");
        private static final Identifier VEHICLE_FULL_HEART_TEXTURE = Identifier.ofVanilla("hud/heart/vehicle_full");
        private static final Identifier VEHICLE_HALF_HEART_TEXTURE = Identifier.ofVanilla("hud/heart/vehicle_half");
        private static final Identifier ID = Identifier.ofVanilla("health_renderer");

        //? if >=1.21.6 {
        /^RenderPipeline LAYER = RenderPipelines.GUI_TEXTURED;
        ^///?} else {
        Function<Identifier, RenderLayer> LAYER = RenderLayer::getGuiTextured;
         //?}
        *///?}

        public VanillaHealthStatusBarRenderer() {
            super(ID, ICONS, StatusBarRenderer.Position.LEFT, StatusBarRenderer.Direction.L2R);
        }

        @Override
        public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int x, int y, StatusBarLogic logic) {
            //? if =1.20.1 {
            boolean bl = client.inGameHud.heartJumpEndTick > (long)client.inGameHud.getTicks() && (client.inGameHud.heartJumpEndTick - (long)client.inGameHud.getTicks()) / 3L % 2L == 1L;

            float f = Math.max((float)playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float)Math.max(client.inGameHud.renderHealthValue, MathHelper.ceil(playerEntity.getHealth())));
            int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
            int q = MathHelper.ceil((f + (float)p) / 2.0F / 10.0F);
            int r = Math.max(10 - (q - 2), 3);
            int v = -1;
            if (playerEntity.hasStatusEffect(StatusEffects.REGENERATION)) {
                v = client.inGameHud.getTicks() % MathHelper.ceil(f + 5.0F);
            }

            this.renderHealthBar(client, context, playerEntity, x, y, r, v, f, MathHelper.ceil(playerEntity.getHealth()), client.inGameHud.renderHealthValue, p, bl);
            //?} else {
            /*boolean blinking = client.inGameHud.heartJumpEndTick > (long)client.inGameHud.getTicks() && (client.inGameHud.heartJumpEndTick - (long)client.inGameHud.getTicks()) / 3L % 2L == 1L;
            int health = client.inGameHud.renderHealthValue;
            int lastHealth = MathHelper.ceil(playerEntity.getHealth());
            int absorption = MathHelper.ceil(playerEntity.getAbsorptionAmount());
            float maxHealth = Math.max((float)playerEntity.getAttributeValue(EntityAttributes.MAX_HEALTH), (float)Math.max(health, lastHealth));

            int p22 = MathHelper.ceil((maxHealth + (float)absorption) / 2.0F / 10.0F);
            int lines = Math.max(10 - (p22 - 2), 3);

            int regeneratingHeartIndex = -1;
            if (playerEntity.hasStatusEffect(StatusEffects.REGENERATION)) {
                regeneratingHeartIndex = client.inGameHud.getTicks() % MathHelper.ceil(maxHealth + 5.0F);
            }

            InGameHud.HeartType heartType = InGameHud.HeartType.fromPlayerState(playerEntity);
            boolean bl = playerEntity.getWorld().getLevelProperties().isHardcore();
            int i = MathHelper.ceil((double)maxHealth / (double)2.0F);
            int j = MathHelper.ceil((double)absorption / (double)2.0F);
            int k = i * 2;

            for(int l = i + j - 1; l >= 0; --l) {
                int m = l / 10;
                int n = l % 10;
                //int o = x + n * 8;
                int o = x + (getDirection().equals(Direction.L2R) ? (getPosition().equals(Position.RIGHT) ? -72 : 0) + n * 8 : (getPosition().equals(Position.LEFT) ? 72 : 0) + -(n * 8));
                int p = y - m * lines;
                if (lastHealth + absorption <= 4) {
                    p += client.inGameHud.random.nextInt(2);
                }

                if (l < i && l == regeneratingHeartIndex) {
                    p -= 2;
                }

                this.drawHeart(context, InGameHud.HeartType.CONTAINER, o, p, bl, blinking, false);
                int q = l * 2;
                boolean bl2 = l >= i;
                if (bl2) {
                    int r = q - k;
                    if (r < absorption) {
                        boolean bl3 = r + 1 == absorption;
                        this.drawHeart(context, heartType == InGameHud.HeartType.WITHERED ? heartType : InGameHud.HeartType.ABSORBING, o, p, bl, false, bl3);
                    }
                }

                if (blinking && q < health) {
                    boolean bl4 = q + 1 == health;
                    this.drawHeart(context, heartType, o, p, bl, true, bl4);
                }

                if (q < lastHealth) {
                    boolean bl4 = q + 1 == lastHealth;
                    this.drawHeart(context, heartType, o, p, bl, false, bl4);
                }
            }
            *///?}
        }

        //? if =1.20.1 {
        private void renderHealthBar(MinecraftClient client, DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking) {
            InGameHud.HeartType heartType = InGameHud.HeartType.fromPlayerState(player);
            int i = 9 * (player.getWorld().getLevelProperties().isHardcore() ? 5 : 0);
            int j = MathHelper.ceil((double)maxHealth / (double)2.0F);
            int k = MathHelper.ceil((double)absorption / (double)2.0F);
            int l = j * 2;

            for(int m = j + k - 1; m >= 0; --m) {
                int n = m / 10;
                int o = m % 10;
                //int p = x + o * 8;
                int p = x + (getDirection().equals(Direction.L2R) ? (getPosition().equals(Position.RIGHT) ? -72 : 0) + o * 8 : (getPosition().equals(Position.LEFT) ? 72 : 0) + -(o * 8));

                int q = y - n * lines;
                if (lastHealth + absorption <= 4) {
                    q += client.inGameHud.random.nextInt(2);
                }

                if (m < j && m == regeneratingHeartIndex) {
                    q -= 2;
                }

                this.drawHeart(context, InGameHud.HeartType.CONTAINER, p, q, i, blinking, false);
                int r = m * 2;
                boolean bl = m >= j;
                if (bl) {
                    int s = r - l;
                    if (s < absorption) {
                        boolean bl2 = s + 1 == absorption;
                        this.drawHeart(context, heartType == InGameHud.HeartType.WITHERED ? heartType : InGameHud.HeartType.ABSORBING, p, q, i, false, bl2);
                    }
                }

                if (blinking && r < health) {
                    boolean bl3 = r + 1 == health;
                    this.drawHeart(context, heartType, p, q, i, true, bl3);
                }

                if (r < lastHealth) {
                    boolean bl3 = r + 1 == lastHealth;
                    this.drawHeart(context, heartType, p, q, i, false, bl3);
                }
            }
        }
        private void drawHeart(DrawContext context, InGameHud.HeartType type, int x, int y, int v, boolean blinking, boolean halfHeart) {
            context.drawTexture(ICONS, x, y, type.getU(halfHeart, blinking), v, 9, 9);
        }
        //?}

        //? if >=1.21.1 {
        /*private void drawHeart(DrawContext context, InGameHud.HeartType type, int x, int y, boolean hardcore, boolean blinking, boolean half) {
            context.drawGuiTexture(LAYER, type.getTexture(hardcore, half, blinking), x, y, 9, 9);
        }
        *///?}

        @Override
        public float getHeight(MinecraftClient client, PlayerEntity playerEntity) {
            //? if =1.20.1 {
            float f = Math.max((float)playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float)Math.max(client.inGameHud.renderHealthValue, MathHelper.ceil(playerEntity.getHealth())));
            int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
            int q = MathHelper.ceil((f + (float)p) / 2.0F / 10.0F);
            int r = Math.max(10 - (q - 2), 3);
            int s = (q - 1) * r;
            return 10 + s;
            //?} else {
            /*int health = client.inGameHud.renderHealthValue;
            int lastHealth = MathHelper.ceil(playerEntity.getHealth());
            int absorption = MathHelper.ceil(playerEntity.getAbsorptionAmount());
            float maxHealth = Math.max((float)playerEntity.getAttributeValue(EntityAttributes.MAX_HEALTH), (float)Math.max(health, lastHealth));
            int p22 = MathHelper.ceil((maxHealth + (float)absorption) / 2.0F / 10.0F);
            int lines = Math.max(10 - (p22 - 2), 3);

            int s = (p22 - 1) * lines;

            return 10 + s;
            *///?}
        }
    }
}
