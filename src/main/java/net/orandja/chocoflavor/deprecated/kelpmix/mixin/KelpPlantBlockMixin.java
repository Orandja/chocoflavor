//package net.orandja.chocoflavor.mods.kelpmix.mixin;
//
//import net.minecraft.block.*;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.stat.Stats;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.util.math.random.Random;
//import net.minecraft.util.shape.VoxelShape;
//import net.minecraft.world.World;
//import org.jetbrains.annotations.Nullable;
//import org.spongepowered.asm.mixin.Mixin;
//
//@SuppressWarnings("unused")
//@Mixin(KelpPlantBlock.class)
//public abstract class KelpPlantBlockMixin extends AbstractPlantBlock implements FluidFillable {
//    protected KelpPlantBlockMixin(Settings settings, Direction direction, VoxelShape voxelShape, boolean bl) {
//        super(settings, direction, voxelShape, bl);
//    }
//
//    @Override
//    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
//        player.incrementStat(Stats.MINED.getOrCreateStat(this));
//        player.addExhaustion(0.005f);
//        afterKelpBreak(this, world, player, pos, state, blockEntity, stack);
//    }
//
//    @Override
//    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
//        if (!state.canPlaceAt(world, pos)) {
//            afterKelpBreak(this, world, null, pos, state, null, ItemStack.EMPTY);
//            world.setBlockState(pos, Blocks.WATER.getDefaultState(), 2);
//        }
//    }
//
//    public void afterKelpBreak(Block block, World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
//        if(world instanceof ServerWorld serverWorld) {
//            Block.getDroppedStacks(state, serverWorld, pos, blockEntity, player, stack).forEach(itemStack -> {
//                BlockPos upperPos = pos;
//                while(vw$isKelp(world, upperPos = upperPos.offset(Direction.Axis.Y, 1))) {
//                    itemStack.increment(1);
//                    world.setBlockState(upperPos, Blocks.WATER.getDefaultState(), 2);
//                }
//                Block.dropStack(world, pos, itemStack);
//            });
//            state.onStacksDropped(serverWorld, pos, stack, true);
//        }
//    }
//
//
//    boolean vw$isKelp(World world, BlockPos pos) {
//        BlockState state = world.getBlockState(pos);
//        return state.isOf(getPlant()) || state.isOf(Blocks.KELP);
//    }
//}
