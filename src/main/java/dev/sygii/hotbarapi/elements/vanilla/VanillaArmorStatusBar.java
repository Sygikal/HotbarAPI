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

public class VanillaArmorStatusBar {

    public static class VanillaArmorStatusBarRenderer extends StatusBarRenderer {

        private static final Identifier ICONS = new Identifier("textures/gui/icons.png");

        public VanillaArmorStatusBarRenderer() {
            super(new Identifier("armor_renderer"), ICONS, StatusBarRenderer.Position.LEFT, StatusBarRenderer.Direction.L2R);
        }

        @Override
        public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int xPosition, int yPosition, StatusBarLogic logic) {
            //System.out.println(this.getPosition());
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
        }
    }

    public static class VanillaArmorStatusBarLogic extends StatusBarLogic {

        public VanillaArmorStatusBarLogic() {
            super(new Identifier("armor_logic"), (ent) -> 0, (ent) -> 0);
        }

        @Override
        public boolean isVisible(MinecraftClient client, PlayerEntity playerEntity) {
            return playerEntity.getArmor() > 1;
        }
    }
}
