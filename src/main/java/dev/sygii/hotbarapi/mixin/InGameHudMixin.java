package dev.sygii.hotbarapi.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.StatusBar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
@Debug(export=true)
public class InGameHudMixin {
    @Unique
    private static final Identifier CUSTOM_HEART = Identifier.of(HotbarAPI.MOD_ID, "textures/gui/custom_heart.png");


    @Shadow
    @Mutable
    @Final
    private MinecraftClient client;

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

    private PlayerEntity getCameraPlayer() {
        return !(this.client.getCameraEntity() instanceof PlayerEntity) ? null : (PlayerEntity)this.client.getCameraEntity();
    }

    /*@ModifyConstant(method = "renderExperienceBar", constant = @Constant(intValue = 8453920), require = 0)
    private int modifyExperienceNumberColor(int original) {
        if (((LevelManagerAccess) client.player).getLevelManager().hasAvailableLevel()) {
            return 1507303;
        } else {
            return original;
        }
    }*/

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
                    //statusBar.renderStatusBar(client, context, playerEntity, scaledWidth / 2 - 91, scaledWidth / 2 + 91, scaledHeight - 39);
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
