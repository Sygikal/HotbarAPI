package dev.sygii.hotbarapi.elements.vanilla;

import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.elements.StatusBar;
import dev.sygii.hotbarapi.elements.StatusBarLogic;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

//? if >1.21.1 {
/*import net.minecraft.client.render.RenderLayer;
import net.minecraft.sound.SoundEvents;
import java.util.function.Function;
*///?}

//? if >=1.21.6 {
/*import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;
*///?}

public class VanillaAirStatusBar {

    public static class VanillaAirStatusBarRenderer extends StatusBarRenderer {

        //? if =1.20.1 {
        private static final Identifier ICONS = new Identifier("textures/gui/icons.png");
        public static final Identifier ID = new Identifier("air_renderer");
        //?} else {
        /*private static final Identifier ICONS = null;
        private static final Identifier AIR_TEXTURE = Identifier.ofVanilla("hud/air");
        private static final Identifier AIR_BURSTING_TEXTURE = Identifier.ofVanilla("hud/air_bursting");
        private static final Identifier AIR_EMPTY_TEXTURE = Identifier.ofVanilla("hud/air_empty");

        private int lastBurstBubble;
        public static final Identifier ID = Identifier.ofVanilla("air_renderer");
        //? if >=1.21.6 {
        /^RenderPipeline LAYER = RenderPipelines.GUI_TEXTURED;
        ^///?} else {
        Function<Identifier, RenderLayer> LAYER = RenderLayer::getGuiTextured;
         //?}
        *///?}

        public VanillaAirStatusBarRenderer() {
            super(ID, ICONS, StatusBarRenderer.Position.RIGHT, StatusBarRenderer.Direction.R2L);
        }

        @Override
        public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int xPosition, int yPosition, StatusBarLogic logic) {
            //? if =1.20.1 {
            int y = playerEntity.getMaxAir();
            int z = Math.min(playerEntity.getAir(), y);

            if (playerEntity.isSubmergedIn(FluidTags.WATER) || z < y) {
                int ab = MathHelper.ceil((double)(z - 2) * (double)10.0F / (double)y);
                int ac = MathHelper.ceil((double)z * (double)10.0F / (double)y) - ab;

                for(int ad = 0; ad < ab + ac; ++ad) {
                    int x = xPosition + (getDirection().equals(Direction.L2R) ? (getPosition().equals(Position.RIGHT) ? -72 : 0) + ad * 8 : (getPosition().equals(Position.LEFT) ? 72 : 0) + -(ad * 8));

                    if (ad < ab) {
                        context.drawTexture(ICONS, x, yPosition, 16, 18, 9, 9);
                    } else {
                        context.drawTexture(ICONS, x, yPosition, 25, 18, 9, 9);
                    }
                }
            }
            //?} else {
            /*int i = playerEntity.getMaxAir();
            int j = Math.min(i, Math.max(playerEntity.getAir(), 0));
            boolean bl = playerEntity.isSubmergedIn(FluidTags.WATER);
            if (bl || j < i) {
                //top = this.getAirBubbleY(heartCount, top);
                int k = getAirBubbles(j, i, -2);
                int l = getAirBubbles(j, i, 0);
                int m = 10 - getAirBubbles(j, i, getAirBubbleDelay(j, bl));
                boolean bl2 = k != l;
                if (!bl) {
                    this.lastBurstBubble = 0;
                }

                for(int n = 1; n <= 10; ++n) {
                    int o = xPosition + (getDirection().equals(Direction.L2R) ? (getPosition().equals(Position.RIGHT) ? -72 : 0) + n * 8 : (getPosition().equals(Position.LEFT) ? 72 : 0) + -((n-1) * 8));

                    //int o = xPosition - (n - 1) * 8 - 9;
                    if (n <= k) {
                        context.drawGuiTexture(LAYER, AIR_TEXTURE, o, yPosition, 9, 9);
                    } else if (bl2 && n == l && bl) {
                        context.drawGuiTexture(LAYER, AIR_BURSTING_TEXTURE, o, yPosition, 9, 9);
                        this.playBurstSound(n, playerEntity, m);
                    } else if (n > 10 - m) {
                        int p = m == 10 && client.inGameHud.getTicks() % 2 == 0 ? client.inGameHud.random.nextInt(2) : 0;
                        context.drawGuiTexture(LAYER, AIR_EMPTY_TEXTURE, o, yPosition + p, 9, 9);
                    }
                }
            }
            *///?}
        }

        //? if >=1.21.1 {
        /*private static int getAirBubbles(int air, int maxAir, int delay) {
            return MathHelper.ceil((float)((air + delay) * 10) / (float)maxAir);
        }

        private static int getAirBubbleDelay(int air, boolean submergedInWater) {
            return air != 0 && submergedInWater ? 1 : 0;
        }

        private void playBurstSound(int bubble, PlayerEntity player, int burstBubbles) {
            if (this.lastBurstBubble != bubble) {
                float f = 0.5F + 0.1F * (float)Math.max(0, burstBubbles - 3 + 1);
                float g = 1.0F + 0.1F * (float)Math.max(0, burstBubbles - 5 + 1);
                player.playSound(SoundEvents.UI_HUD_BUBBLE_POP, f, g);
                this.lastBurstBubble = bubble;
            }

        }
        *///?}
    }

    public static class VanillaAirStatusBarLogic extends StatusBarLogic {

        //? if =1.20.1 {
        public static final Identifier ID = new Identifier("air_logic");
         //?} else {
        /*public static final Identifier ID = Identifier.ofVanilla("air_logic");
        *///?}

        public VanillaAirStatusBarLogic() {
            super(ID, (ent) -> 0, (ent) -> 0);
        }

        @Override
        public boolean isVisible(MinecraftClient client, PlayerEntity playerEntity) {
            int y = playerEntity.getMaxAir();
            //? if =1.20.1 {
            int z = Math.min(playerEntity.getAir(), y);
             //?} else {
            /*int z = Math.min(y, Math.max(playerEntity.getAir(), 0));
            *///?}
            boolean bl = playerEntity.isSubmergedIn(FluidTags.WATER);
            return bl || z < y;
        }
    }
}
