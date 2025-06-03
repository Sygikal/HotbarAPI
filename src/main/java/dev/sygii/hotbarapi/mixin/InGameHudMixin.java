package dev.sygii.hotbarapi.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.elements.StatusBar;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Shadow
    @Mutable
    @Final
    private MinecraftClient client;

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 2))
    private void addBars(DrawContext context, CallbackInfo ci, @Local PlayerEntity playerEntity, @Local(ordinal = 3) int m, @Local(ordinal = 4) int n, @Local(ordinal = 5) int o) {

        for (StatusBar statusBar : HotbarAPI.statusBars) {
            Profilers.get().swap(statusBar.getId().toString());

            if (statusBar.isVisible(client, playerEntity) && !statusBar.isImportant()) {
                //statusBar.renderStatusBar(client, context, playerEntity, scaledWidth / 2 - 91, scaledWidth / 2 + 91, scaledHeight - 39);
                int xPos = statusBar.getPosition().equals(StatusBar.Position.LEFT) ? context.getScaledWindowWidth() / 2 - 91 : context.getScaledWindowWidth() / 2 + 91;
                statusBar.render(client, context, playerEntity, xPos, (int) ( context.getScaledWindowHeight() - 39 - HotbarAPI.getHeightOffest(client, statusBar, playerEntity)));
            }
        }
    }

    @Inject(method = "renderMainHud", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountHealth(Lnet/minecraft/client/gui/DrawContext;)V"))
    private void stopRenderMount(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        PlayerEntity playerEntity = ((InGameHudInvoker) this).invokeGetCameraPlayer();
        if (playerEntity != null) {
            for (StatusBar statusBar : HotbarAPI.statusBars) {
                Profilers.get().swap(statusBar.getId().toString());
                if (statusBar.isVisible(client, playerEntity) && statusBar.isImportant()) {
                    int xPos = statusBar.getPosition().equals(StatusBar.Position.LEFT) ? context.getScaledWindowWidth() / 2 - 91 : context.getScaledWindowWidth() / 2 + 91;
                    statusBar.render(client, context, playerEntity, xPos, (int) ( context.getScaledWindowHeight() - 39 - HotbarAPI.getHeightOffest(client, statusBar, playerEntity)));
                }
            }
        }
    }

    @Inject(method = "getAirBubbleY", at = @At(value = "HEAD"), cancellable = true)
    private void sex(int heartCount, int top, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(top);
        cir.cancel();
    }

    @WrapWithCondition(
            method = "renderMainHud",
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

    @WrapWithCondition(
            method = "renderStatusBars",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderArmor(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIII)V")
    )
    private boolean onlyRenderIfAllowed(DrawContext context, PlayerEntity player, int i, int j, int k, int x) {
        return false;
    }

    @WrapWithCondition(
            method = "renderStatusBars",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderFood(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;II)V")
    )
    private boolean onlyRenderIfAllowed(InGameHud instance, DrawContext context, PlayerEntity player, int top, int right) {
        return false;
    }

    @WrapWithCondition(
            method = "renderStatusBars",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderAirBubbles(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;III)V")
    )
    private boolean onlyRenderIfAllowed(InGameHud instance, DrawContext context, PlayerEntity player, int heartCount, int top, int left) {
        return false;
    }
}
