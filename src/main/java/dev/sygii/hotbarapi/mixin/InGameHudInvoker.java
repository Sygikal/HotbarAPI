package dev.sygii.hotbarapi.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(InGameHud.class)
public interface InGameHudInvoker {
    @Invoker("getCameraPlayer")
    public PlayerEntity invokeGetCameraPlayer();
}
