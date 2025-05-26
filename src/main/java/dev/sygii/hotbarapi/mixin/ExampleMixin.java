package dev.sygii.hotbarapi.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(World.class)
public class ExampleMixin {

	@ModifyReturnValue(at = @At(value = "RETURN"), method = "getSpawnPos")
	private BlockPos init(BlockPos original) {
		return original.add(0, 100, 0);
	}
}