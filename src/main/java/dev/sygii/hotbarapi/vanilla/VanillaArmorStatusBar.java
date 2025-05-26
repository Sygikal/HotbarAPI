package dev.sygii.hotbarapi.vanilla;

import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.StatusBar;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class VanillaArmorStatusBar extends StatusBar {
    private static final Identifier ICONS = new Identifier("textures/gui/icons.png");

    public VanillaArmorStatusBar() {
        super(new Identifier("armor"), Identifier.of(HotbarAPI.MOD_ID, "textures/gui/sex.png"), Position.LEFT, Direction.R2L);
    }

    @Override
    public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int xPosition, int yPosition) {
        float f = Math.max((float)playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float)Math.max(client.inGameHud.renderHealthValue, MathHelper.ceil(playerEntity.getHealth())));
        int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
        int q = MathHelper.ceil((f + (float)p) / 2.0F / 10.0F);
        int r = Math.max(10 - (q - 2), 3);
        //s = o - (q - 1) * r - 10;
        int u = playerEntity.getArmor();
        for(int w = 0; w < 10; ++w) {
            if (u > 0) {
                int x = xPosition + w * 8;
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

    @Override
    public boolean isVisible(MinecraftClient client, PlayerEntity playerEntity) {
        return playerEntity.getArmor() > 0;
    }
}
