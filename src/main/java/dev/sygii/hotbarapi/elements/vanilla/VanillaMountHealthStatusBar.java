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

public class VanillaMountHealthStatusBar{

    public static class VanillaMountHealthStatusBarRenderer extends StatusBarRenderer {

        private static final Identifier ICONS = new Identifier("textures/gui/icons.png");

        public VanillaMountHealthStatusBarRenderer() {
            super(new Identifier("mount_health_renderer"), ICONS, StatusBarRenderer.Position.RIGHT, StatusBarRenderer.Direction.R2L);
        }

        @Override
        public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int xPosition, int yPosition, StatusBarLogic logic) {
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
                            int s = xPosition - p * 8;
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

        public VanillaMountHealthStatusBarLogic() {
            super(new Identifier("mount_health_logic"), (ent) -> 0, (ent) -> 0);
        }

        @Override
        public boolean isVisible(MinecraftClient client, PlayerEntity playerEntity) {
            LivingEntity livingEntity = VanillaMountHealthStatusBar.VanillaMountHealthStatusBarRenderer.getRiddenEntity(client);
            int x = VanillaMountHealthStatusBar.VanillaMountHealthStatusBarRenderer.getHeartCount(livingEntity);
            return x > 0;
        }
    }
}
