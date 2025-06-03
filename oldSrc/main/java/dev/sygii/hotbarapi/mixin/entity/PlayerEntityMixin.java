package dev.sygii.hotbarapi.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sygii.hotbarapi.access.PlayerEntityAccessor;
import dev.sygii.hotbarapi.util.ColorUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PlayerEntityAccessor {
    @Unique
    private int stamina = 40;

    @Unique
    private int staminaTicks;

    @Inject(method = "jump", at = @At(value = "HEAD"), cancellable = true)
    private void jump(CallbackInfo ci) {
        stamina--;
        if (getStamina() < 0) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void tick(CallbackInfo ci) {
        //if (!((PlayerEntity)(Object)this).getWorld().isClient) {
            staminaTicks++;
            if (staminaTicks > 20) {
                if (getStamina() < 40) {
                    stamina++;
                }
                staminaTicks = 0;
            }
            //System.out.println(getStamina());
        //}
    }

    @Unique
    public int getStamina() {
        return stamina;
    }
}
