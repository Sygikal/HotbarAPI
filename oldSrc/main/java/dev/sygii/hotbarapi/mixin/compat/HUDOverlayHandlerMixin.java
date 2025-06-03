package dev.sygii.hotbarapi.mixin.compat;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import squeek.appleskin.client.HUDOverlayHandler;

@Mixin(HUDOverlayHandler.class)
public interface HUDOverlayHandlerMixin {

    @Invoker("generateBarOffsets")
    public void invokeGenerateBarOffsets(int top, int left, int right, int ticks, PlayerEntity player);
}
