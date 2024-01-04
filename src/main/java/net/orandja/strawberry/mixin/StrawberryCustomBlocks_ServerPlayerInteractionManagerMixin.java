package net.orandja.strawberry.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.orandja.strawberry.block.StrawberryBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class StrawberryCustomBlocks_ServerPlayerInteractionManagerMixin {

    @Shadow private int tickCounter;

    @Shadow @Final protected ServerPlayerEntity player;

    @Shadow private int blockBreakingProgress;

    @Shadow protected ServerWorld world;

    @Shadow private int startMiningTime;

    @Shadow private boolean mining;

    @Shadow public abstract void finishMining(BlockPos pos, int sequence, String reason);

    @Inject(method = "continueMining", at = @At(value = "HEAD"), cancellable = true)
    public void continueMining(BlockState state, BlockPos pos, int failedStartMiningTime, CallbackInfoReturnable<Float> info) {
        int i = this.tickCounter - failedStartMiningTime;
        float f = state.calcBlockBreakingDelta(this.player, this.player.getServerWorld(), pos) * (float)(i + 1);
        int j = (int)(f * 10.0F);
        this.world.setBlockBreakingInfo(this.player.getId(), pos, j);
        this.blockBreakingProgress = j;

        if(this.world.getBlockState(pos.up()).getBlock() instanceof StrawberryBlock && blockBreakingProgress >= 8) {
            this.world.breakBlock(pos, true);
        }

        info.setReturnValue(f);
    }
}
