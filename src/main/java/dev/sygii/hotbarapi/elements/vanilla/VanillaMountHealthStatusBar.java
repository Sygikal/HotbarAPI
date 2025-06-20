package dev.sygii.hotbarapi.elements.vanilla;

import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.elements.StatusBar;
import dev.sygii.hotbarapi.elements.StatusBarLogic;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
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

public class VanillaMountHealthStatusBar{

    public static class VanillaMountHealthStatusBarRenderer extends StatusBarRenderer {

        //? if =1.20.1 {
        private static final Identifier ICONS = new Identifier("textures/gui/icons.png");
        private static final Identifier ID = new Identifier("mount_health_renderer");
        //?} else {
        /*private static final Identifier ICONS = null;
        private static final Identifier VEHICLE_CONTAINER_HEART_TEXTURE = Identifier.ofVanilla("hud/heart/vehicle_container");
        private static final Identifier VEHICLE_FULL_HEART_TEXTURE = Identifier.ofVanilla("hud/heart/vehicle_full");
        private static final Identifier VEHICLE_HALF_HEART_TEXTURE = Identifier.ofVanilla("hud/heart/vehicle_half");
        private static final Identifier ID = Identifier.ofVanilla("mount_health_renderer");

        //? if >=1.21.6 {
        /^RenderPipeline LAYER = RenderPipelines.GUI_TEXTURED;
        ^///?} else {
        Function<Identifier, RenderLayer> LAYER = RenderLayer::getGuiTextured;
         //?}
        *///?}

        public VanillaMountHealthStatusBarRenderer() {
            super(ID, ICONS, StatusBarRenderer.Position.RIGHT, StatusBarRenderer.Direction.R2L);
        }

        @Override
        public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int xPosition, int yPosition, StatusBarLogic logic) {
            //? if =1.20.1 {
            LivingEntity livingEntity = getRiddenEntity(client);
            if (livingEntity != null) {
                int i = getHeartCount(livingEntity);
                if (i != 0) {
                    int j = (int)Math.ceil((double)livingEntity.getHealth());
                    int m = yPosition;
                    int n = 0;

                    for(; i > 0; n += 20) {
                        int o = Math.min(i, 10);
                        i -= o;

                        for(int p = 0; p < o; ++p) {
                            int q = 52;
                            int r = 0;
                            int s = xPosition + (getDirection().equals(Direction.L2R) ? (getPosition().equals(Position.RIGHT) ? -72 : 0) + p * 8 : (getPosition().equals(Position.LEFT) ? 72 : 0) + -(p * 8));

                            context.drawTexture(ICONS, s, m, 52, 9, 9, 9);
                            if (p * 2 + 1 + n < j) {
                                context.drawTexture(ICONS, s, m, 88, 9, 9, 9);
                            }

                            if (p * 2 + 1 + n == j) {
                                context.drawTexture(ICONS, s, m, 97, 9, 9, 9);
                            }
                        }

                        m -= 10;
                    }

                }
            }
            //?} else {
            /*LivingEntity livingEntity = getRiddenEntity(client);
            if (livingEntity != null) {
                int i = getHeartCount(livingEntity);
                if (i != 0) {
                    int j = (int)Math.ceil((double)livingEntity.getHealth());
                    int k = yPosition;
                    int l = xPosition;
                    int m = k;

                    for(int n = 0; i > 0; n += 20) {
                        int o = Math.min(i, 10);
                        i -= o;

                        for(int p = 0; p < o; ++p) {
                            //int q = l - p * 8 - 9;
                            int q = xPosition + (getDirection().equals(Direction.L2R) ? (getPosition().equals(Position.RIGHT) ? -72 : 0) + p * 8 : (getPosition().equals(Position.LEFT) ? 72 : 0) + -(p * 8));

                            context.drawGuiTexture(LAYER, VEHICLE_CONTAINER_HEART_TEXTURE, q, m, 9, 9);
                            if (p * 2 + 1 + n < j) {
                                context.drawGuiTexture(LAYER, VEHICLE_FULL_HEART_TEXTURE, q, m, 9, 9);
                            }

                            if (p * 2 + 1 + n == j) {
                                context.drawGuiTexture(LAYER, VEHICLE_HALF_HEART_TEXTURE, q, m, 9, 9);
                            }
                        }

                        m -= 10;
                    }

                }
            }
            *///?}
        }

        private static int getHeartCount(LivingEntity entity) {
            if (entity != null && entity.isLiving()) {
                float f = entity.getMaxHealth();
                int i = (int)(f + 0.5F) / 2;
                if (i > 30) {
                    i = 30;
                }

                return i;
            } else {
                return 0;
            }
        }

        private int getHeartRows(int heartCount) {
            return (int)Math.ceil((double)heartCount / (double)10.0F);
        }

        private static LivingEntity getRiddenEntity(MinecraftClient client) {
            PlayerEntity playerEntity = getCameraPlayer(client);
            if (playerEntity != null) {
                Entity entity = playerEntity.getVehicle();
                if (entity == null) {
                    return null;
                }

                if (entity instanceof LivingEntity) {
                    return (LivingEntity)entity;
                }
            }

            return null;
        }

        private static PlayerEntity getCameraPlayer(MinecraftClient client) {
            return !(client.getCameraEntity() instanceof PlayerEntity) ? null : (PlayerEntity)client.getCameraEntity();
        }

        @Override
        public float getHeight(MinecraftClient client, PlayerEntity playerEntity) {
            LivingEntity livingEntity = getRiddenEntity(client);
            int x = getHeartCount(livingEntity);
            int aa = this.getHeartRows(x);
            return aa * 10;
        }
    }

    public static class VanillaMountHealthStatusBarLogic extends StatusBarLogic {
        //? if =1.20.1 {
        private static final Identifier ID = new Identifier("mount_health_logic");
        //?} else {
        /*private static final Identifier ID = Identifier.ofVanilla("mount_health_logic");
        *///?}

        public VanillaMountHealthStatusBarLogic() {
            super(ID, (ent) -> 0, (ent) -> 0);
        }

        @Override
        public boolean isVisible(MinecraftClient client, PlayerEntity playerEntity) {
            LivingEntity livingEntity = VanillaMountHealthStatusBar.VanillaMountHealthStatusBarRenderer.getRiddenEntity(client);
            int x = VanillaMountHealthStatusBar.VanillaMountHealthStatusBarRenderer.getHeartCount(livingEntity);
            return x > 0;
        }
    }
}
