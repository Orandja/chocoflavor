//package net.orandja.chocoflavor.mods.core.mixin;
//
//import net.minecraft.block.BlockState;
//import net.minecraft.block.LeavesBlock;
//import net.minecraft.block.Waterloggable;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.random.Random;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(LeavesBlock.class)
//public abstract class LeavesBlockMixin implements Waterloggable {
//
//    @Shadow
//    public abstract void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
//
//    @Inject(method = "scheduledTick", at = @At("TAIL"))
//    public void forceScheduledTickToCallRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo info) {
//        randomTick(state, world, pos, random);
//    }
//}
