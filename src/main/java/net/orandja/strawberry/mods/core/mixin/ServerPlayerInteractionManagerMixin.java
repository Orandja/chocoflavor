package net.orandja.strawberry.mods.core.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.orandja.chocoflavor.utils.Utils;
import net.orandja.strawberry.mods.core.block.StrawberryBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Shadow private int tickCounter;

    @Shadow @Final protected ServerPlayerEntity player;

    @Shadow private int blockBreakingProgress;

    @Shadow protected ServerWorld world;

//    @Inject(method = "continueMining", at = @At(value = "RETURN"))
//    public void alsoUpdateLocal(BlockState state, BlockPos pos, int failedStartMiningTime, CallbackInfoReturnable<Float> cir) {
//        if(world.getBlockState(pos).getBlock() instanceof StrawberryBlock) {
//            player.networkHandler.sendPacket(new BlockBreakingProgressS2CPacket(player.getId(), pos, blockBreakingProgress));
//        }
//
//        // https://www.reddit.com/r/fabricmc/comments/1018xqq/custom_block_breaking_system/
//        // https://github.com/Enecske/customBlock-core/blob/1.19.x/src/main/java/net/enecske/customblock_core/core/CustomBlock.java
//    }
    @Inject(method = "continueMining", at = @At(value = "HEAD"), cancellable = true)
    public void continueMining(BlockState state, BlockPos pos, int failedStartMiningTime, CallbackInfoReturnable<Float> info) {
        int i = this.tickCounter - failedStartMiningTime;
        float f = state.calcBlockBreakingDelta(this.player, this.player.getServerWorld(), pos) * (float)(i + 1);
        int j = (int)(f * 10.0F);
        this.world.setBlockBreakingInfo(this.player.getId(), pos, j);
        this.blockBreakingProgress = j;

        info.setReturnValue(f);
    }
}
