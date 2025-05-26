package dev.sygii.hotbarapi.vanilla;

import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.HudElement;
import dev.sygii.hotbarapi.StatusBar;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.util.List;

public class VanillaAirStatusBar extends StatusBar {
    private static final Identifier ICONS = new Identifier("textures/gui/icons.png");

    public VanillaAirStatusBar() {
        super(new Identifier("air"), Identifier.of(HotbarAPI.MOD_ID, "textures/gui/sex.png"), StatusBar.Position.RIGHT, StatusBar.Direction.R2L);
    }

    @Override
    public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int xPosition, int yPosition) {
        int y = playerEntity.getMaxAir();
        int z = Math.min(playerEntity.getAir(), y);

        if (playerEntity.isSubmergedIn(FluidTags.WATER) || z < y) {
            int ab = MathHelper.ceil((double)(z - 2) * (double)10.0F / (double)y);
            int ac = MathHelper.ceil((double)z * (double)10.0F / (double)y) - ab;

            for(int ad = 0; ad < ab + ac; ++ad) {
                if (ad < ab) {
                    context.drawTexture(ICONS, xPosition - ad * 8, yPosition, 16, 18, 9, 9);
                } else {
                    context.drawTexture(ICONS, xPosition - ad * 8, yPosition, 25, 18, 9, 9);
                }
            }
        }
    }

    @Override
    public boolean isVisible(MinecraftClient client, PlayerEntity playerEntity) {
        int y = playerEntity.getMaxAir();
        int z = Math.min(playerEntity.getAir(), y);
        return playerEntity.isSubmergedIn(FluidTags.WATER) || z < y;
    }
}
