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

public class VanillaAirStatusBar {

    public static class VanillaAirStatusBarRenderer extends StatusBarRenderer {

        private static final Identifier ICONS = new Identifier("textures/gui/icons.png");

        public VanillaAirStatusBarRenderer() {
            super(new Identifier("air_renderer"), ICONS, StatusBarRenderer.Position.RIGHT, StatusBarRenderer.Direction.R2L);
        }

        @Override
        public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int xPosition, int yPosition, StatusBarLogic logic) {
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
        }
    }

    public static class VanillaAirStatusBarLogic extends StatusBarLogic {

        public VanillaAirStatusBarLogic() {
            super(new Identifier("air_logic"), (ent) -> 0, (ent) -> 0);
        }

        @Override
        public boolean isVisible(MinecraftClient client, PlayerEntity playerEntity) {
            int y = playerEntity.getMaxAir();
            int z = Math.min(playerEntity.getAir(), y);
            return playerEntity.isSubmergedIn(FluidTags.WATER) || z < y;
        }
    }
}
