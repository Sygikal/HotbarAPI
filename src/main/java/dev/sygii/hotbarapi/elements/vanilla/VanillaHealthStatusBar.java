package dev.sygii.hotbarapi.elements.vanilla;

import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.elements.StatusBar;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.structure.rule.AxisAlignedLinearPosRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class VanillaHealthStatusBar extends StatusBar {
    //private static final Identifier ICONS = new Identifier("textures/gui/icons.png");

    public VanillaHealthStatusBar() {
        super(Identifier.ofVanilla("health"), Identifier.tryParse(HotbarAPI.MOD_ID, "textures/gui/sex.png"), Position.LEFT, Direction.R2L);
    }

    @Override
    public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int xPosition, int yPosition) {
        boolean bl = client.inGameHud.heartJumpEndTick > (long)client.inGameHud.getTicks() && (client.inGameHud.heartJumpEndTick - (long)client.inGameHud.getTicks()) / 3L % 2L == 1L;

        /*float f = Math.max((float)playerEntity.getAttributeValue(EntityAttributes.MAX_HEALTH), (float)Math.max(client.inGameHud.renderHealthValue, MathHelper.ceil(playerEntity.getHealth())));
        int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
        int q = MathHelper.ceil((f + (float)p) / 2.0F / 10.0F);
        int r = Math.max(10 - (q - 2), 3);
        int v = -1;
        if (playerEntity.hasStatusEffect(StatusEffects.REGENERATION)) {
            v = client.inGameHud.getTicks() % MathHelper.ceil(f + 5.0F);
        }

        this.renderHealthBar(client, context, playerEntity, x, y, r, v, f, MathHelper.ceil(playerEntity.getHealth()), client.inGameHud.renderHealthValue, p, bl);*/
        float f = Math.max((float)playerEntity.getAttributeValue(EntityAttributes.MAX_HEALTH), (float)Math.max(client.inGameHud.renderHealthValue, MathHelper.ceil(playerEntity.getHealth())));
        int o = MathHelper.ceil(playerEntity.getAbsorptionAmount());
        int p = MathHelper.ceil((f + (float)o) / 2.0F / 10.0F);
        int q = Math.max(10 - (p - 2), 3);
        int s = -1;
        if (playerEntity.hasStatusEffect(StatusEffects.REGENERATION)) {
            s = client.inGameHud.getTicks() % MathHelper.ceil(f + 5.0F);
        }

        client.inGameHud.renderHealthBar(context, playerEntity, xPosition, yPosition, q, s, f, MathHelper.ceil(playerEntity.getHealth()), client.inGameHud.renderHealthValue, o, bl);

    }

    private void renderHealthBar(MinecraftClient client, DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking) {
        /*InGameHud.HeartType heartType = InGameHud.HeartType.fromPlayerState(player);
        int i = 9 * (player.getWorld().getLevelProperties().isHardcore() ? 5 : 0);
        int j = MathHelper.ceil((double)maxHealth / (double)2.0F);
        int k = MathHelper.ceil((double)absorption / (double)2.0F);
        int l = j * 2;

        for(int m = j + k - 1; m >= 0; --m) {
            int n = m / 10;
            int o = m % 10;
            int p = x + o * 8;
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
        }*/
    }

    /*private void drawHeart(DrawContext context, InGameHud.HeartType type, int x, int y, int v, boolean blinking, boolean halfHeart) {
        context.drawTexture(ICONS, x, y, type.getU(halfHeart, blinking), v, 9, 9);
    }*/

    @Override
    public float getHeight(MinecraftClient client, PlayerEntity playerEntity) {
        float f = Math.max((float)playerEntity.getAttributeValue(EntityAttributes.MAX_HEALTH), (float)Math.max(client.inGameHud.renderHealthValue, MathHelper.ceil(playerEntity.getHealth())));
        int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
        int q = MathHelper.ceil((f + (float)p) / 2.0F / 10.0F);
        int r = Math.max(10 - (q - 2), 3);
        int s = (q - 1) * r;
        return 10 + s;
    }
}
