/*
package dev.sygii.hotbarapi.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.elements.HotbarHighlight;
import dev.sygii.hotbarapi.elements.StatusBar;
import dev.sygii.hotbarapi.access.InGameHudAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.sygii.hotbarapi.util.ColorUtil;

import java.awt.*;
import java.util.Iterator;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
@Debug(export=true)
public abstract class OldInGameHudMixin implements InGameHudAccessor {

    @Shadow
    @Mutable
    @Final
    private MinecraftClient client;

    @Shadow
    @Mutable
    @Final
    private static Identifier WIDGETS_TEXTURE;

    @Shadow
    @Mutable
    @Final
    private Random random;

    @Shadow
    @Mutable
    private int ticks;

    @Shadow
    @Mutable
    private int scaledWidth;
    @Shadow
    @Mutable
    private int scaledHeight;

    @Shadow public abstract TextRenderer getTextRenderer();

    @Unique
    private int hotbarTicks;
    @Unique
    private boolean hotbarSwitch = false;
    @Unique
    private int highlightedSlot = -1;
    @Unique
    private Color highlightedSlotColor = new Color(54, 202, 60, 255);

    private PlayerEntity getCameraPlayer() {
        return !(this.client.getCameraEntity() instanceof PlayerEntity) ? null : (PlayerEntity)this.client.getCameraEntity();
    }

@ModifyConstant(method = "renderExperienceBar", constant = @Constant(intValue = 8453920), require = 0)
    private int modifyExperienceNumberColor(int original) {
        if (((LevelManagerAccess) client.player).getLevelManager().hasAvailableLevel()) {
            return 1507303;
        } else {
            return original;
        }
    }


    @Inject(method = "tick()V", at = @At(value = "HEAD"))
    private void hotbarTick(CallbackInfo ci) {
if (!hotbarSwitch && hotbarTicks < 100) {
            hotbarTicks += 1;
            if (hotbarTicks == 100) {
                hotbarSwitch = true;
            }
        }
        if(hotbarSwitch && hotbarTicks > 0) {
            hotbarTicks -= 1;
            if(hotbarTicks==0) {
                hotbarSwitch = false;
            }
        }

    }

@Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 0))
    private void renderHotBarColor(float tickDelta, DrawContext context, CallbackInfo ci, @Local PlayerEntity playerEntity) {
        int speed = 2000;
        float fadeOffset = (System.currentTimeMillis()) % speed / (speed / 2F);
        Color endColor = new Color(-1);
        Color startColor = new Color(246, 7, 246);
        //float fraction = (float)(ticks % 15) / 15;
        float fraction = (float) ((hotbarTicks * 20)) / (100 * 20);
        //System.out.print(ticks);
        //fraction = Math.min(1.0f, fraction);
        // interpolate between start and end colors with current fraction
        int red = (int)(fraction * endColor.getRed() +
                (1 - fraction) * startColor.getRed());
        int green = (int)(fraction * endColor.getGreen() +
                (1 - fraction) * startColor.getGreen());
        int blue = (int)(fraction * endColor.getBlue() +
                (1 - fraction) * startColor.getBlue());
        //setColor(context, new Color(red, green, blue).getRGB());
        //setColor(context, getRainbow(2000, 1, 1));
        //setColor(context, fade(startColor.getRGB(), endColor.getRGB(), fadeOffset));
    }


@Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getInventory()Lnet/minecraft/entity/player/PlayerInventory;", ordinal = 0))
    private void renderHotBarItemColor(float tickDelta, DrawContext context, CallbackInfo ci, @Local PlayerEntity playerEntity) {
        int speed = 1000;
        float fadeOffset = (System.currentTimeMillis()) % speed / (speed / 2F);
        Color endColor = new Color(-1);
        Color startColor = new Color(246, 7, 246);
        //float fraction = (float)(ticks % 15) / 15;
        float fraction = (float) ((hotbarTicks * 20)) / (100 * 20);
        //System.out.print(ticks);
        //fraction = Math.min(1.0f, fraction);
        // interpolate between start and end colors with current fraction
        int red = (int)(fraction * endColor.getRed() +
                (1 - fraction) * startColor.getRed());
        int green = (int)(fraction * endColor.getGreen() +
                (1 - fraction) * startColor.getGreen());
        int blue = (int)(fraction * endColor.getBlue() +
                (1 - fraction) * startColor.getBlue());
        //setColor(context, new Color(red, green, blue).getRGB());
        //setColor(context, getRainbow(2000, 1, 1));
        //setColor(context, fade(startColor.getRGB(), endColor.getRGB(), fadeOffset));
        int selectedSlot = 5;
        //context.drawTexture(WIDGETS_TEXTURE, this.scaledWidth / 2 - 91 - 1 + selectedSlot * 20, this.scaledHeight - 22 - 1, 0, 22, 24, 22);
        //setColor(context, -1);
    }



    @ModifyExpressionValue(
            method = "renderHeldItemTooltip",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasStatusBars()Z")
    )
    private boolean updateItemTooltipHeight(boolean original) {
        return true;
    }

    @ModifyVariable(method = "renderHeldItemTooltip", at = @At(value = "STORE"), ordinal = 2)
    private int attackKnockbackChanceMixin(int original) {
        PlayerEntity playerEntity = this.getCameraPlayer();
        int offset = 0;
        if (playerEntity != null) {
            offset = Math.round(HotbarAPI.getMaxStatusHeight(client, playerEntity));
        }
        return original + 20 - offset;
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 0), index = 1)
    private float changeActionBarHeight(float og) {
        PlayerEntity playerEntity = this.getCameraPlayer();
        int offset = 0;
        if (playerEntity != null) {
            offset = Math.round(HotbarAPI.getMaxStatusHeight(client, playerEntity));
        }
        return og + 20 - offset;
    }

    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"))
    private void resetHotBarColor(float tickDelta, DrawContext context, CallbackInfo ci, @Local PlayerEntity playerEntity) {
        for (Iterator<Map.Entry<Integer, HotbarHighlight>> it = HotbarAPI.mappedHotbarHighlights.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Integer, HotbarHighlight> hot = it.next();
            if (playerEntity.getInventory().selectedSlot == hot.getKey()) {
                hot.getValue().tick();
            }

            int speed = 1000;
            float fadeOffset = (System.currentTimeMillis()) % speed / (speed / 2F);
            int color = ColorUtil.fade(hot.getValue().getColor().getRGB(), new Color(255, 255, 255, 100).getRGB(), fadeOffset);
            ColorUtil.setColor(context, color);
            if (hot.getValue().getTexture() == null) {
                context.drawTexture(WIDGETS_TEXTURE, this.scaledWidth / 2 - 91 - 1 + hot.getKey() * 20, this.scaledHeight - 22 - 1, 0, 22, 24, 24);
            } else {
                context.drawTexture(hot.getValue().getTexture(), this.scaledWidth / 2 - 91 - 1 + hot.getKey() * 20, this.scaledHeight - 22 - 1, 0, 0, 24, 24, 24, 24);
            }

            if (hot.getValue().getTicksSelected() >= 100) {
                hot.getValue().reset();
                it.remove();
            }
        }
        ColorUtil.setColor(context, -1);
    }

    @Unique
    public void setHighlightedSlotAndColor(int slot, Color color) {
        highlightedSlot = slot;
        if (color != null) {
            highlightedSlotColor = color;
        }
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 2))
    private void addBars(DrawContext context, CallbackInfo ci, @Local PlayerEntity playerEntity, @Local(ordinal = 3) int m, @Local(ordinal = 4) int n, @Local(ordinal = 5) int o) {

        for (StatusBar statusBar : HotbarAPI.statusBars) {
            this.client.getProfiler().swap(statusBar.getId().toString());

            if (statusBar.isVisible(client, playerEntity) && !statusBar.isImportant()) {
                //statusBar.renderStatusBar(client, context, playerEntity, scaledWidth / 2 - 91, scaledWidth / 2 + 91, scaledHeight - 39);
                int xPos = statusBar.getPosition().equals(StatusBar.Position.LEFT) ? scaledWidth / 2 - 91 : scaledWidth / 2 + 91 - 9;
                statusBar.render(client, context, playerEntity, xPos, (int) (scaledHeight - 39 - HotbarAPI.getHeightOffest(client, statusBar, playerEntity)));
            }
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountHealth(Lnet/minecraft/client/gui/DrawContext;)V"))
    private void stopRenderMount(DrawContext context, float tickDelta, CallbackInfo ci) {
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity != null) {
            for (StatusBar statusBar : HotbarAPI.statusBars) {
                this.client.getProfiler().swap(statusBar.getId().toString());
                if (statusBar.isVisible(client, playerEntity) && statusBar.isImportant()) {
                    int xPos = statusBar.getPosition().equals(StatusBar.Position.LEFT) ? scaledWidth / 2 - 91 : scaledWidth / 2 + 91 - 9;
                    statusBar.render(client, context, playerEntity, xPos, (int) (scaledHeight - 39 - HotbarAPI.getHeightOffest(client, statusBar, playerEntity)));
                }
            }
        }
    }

    @WrapWithCondition(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountHealth(Lnet/minecraft/client/gui/DrawContext;)V")
    )
    private boolean onlyRenderIfAllowed(InGameHud instance, DrawContext context) {
        return false;
    }

    @WrapWithCondition(
            method = "renderStatusBars",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V")
    )
    private boolean onlyRenderIfAllowed(InGameHud instance, DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking) {
        return false;
    }

    @ModifyExpressionValue(
            method = "renderStatusBars",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I")
    )
    private int getHeartCount(int original) {
        return 1;
    }

    @ModifyExpressionValue(
            method = "renderStatusBars",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getMaxAir()I")
    )
    private int getMaxAir(int original) {
        return -999;
    }

    @ModifyExpressionValue(
            method = "renderStatusBars",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getArmor()I")
    )
    private int getArmor(int original) {
        return -999;
    }

    @ModifyExpressionValue(
            method = "renderStatusBars",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z")
    )
    private boolean isSubmergedIn(boolean original) {
        return false;
    }
}
*/
