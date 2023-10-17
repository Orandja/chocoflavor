package net.orandja.strawberry.mods.core.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChorusPlantBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChorusPlantBlock.class)
public class ChorusPlantBlockMixin {
    @Inject(method = "scheduledTick", at = @At("HEAD"), cancellable = true)
    public void schdasd(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo info) {
        info.cancel();
    }
}
