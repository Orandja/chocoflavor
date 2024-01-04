//package net.orandja.chocoflavor.mods.core;
//
//import net.minecraft.block.*;
//import net.minecraft.block.dispenser.DispenserBehavior;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.Items;
//import net.minecraft.sound.SoundCategory;
//import net.minecraft.sound.SoundEvents;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.event.GameEvent;
//import net.orandja.chocoflavor.ChocoBuckets;
//import net.orandja.chocoflavor.accessor.DispenserBlockAccessor;
//
//import java.util.Map;
//
//public interface CauldronBucketInteraction {
//
//    static void beforeLaunch() {
//        Map<Item, DispenserBehavior> BEHAVIORS = ((DispenserBlockAccessor)Blocks.DISPENSER).getBEHAVIORS();
//
//        DispenserBehavior WATER_BUCKET_DEFAULT = BEHAVIORS.get(Items.WATER_BUCKET);
//        DispenserBlock.registerBehavior(Items.WATER_BUCKET, (pointer, stack) -> {
//            BlockPos pos;
//            BlockState state = pointer.world().getBlockState(pos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING)));
//            if (state.getBlock() == Blocks.CAULDRON) {
//                pointer.world().setBlockState(pos, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3));
//                pointer.world().playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
//                pointer.world().emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
//
//                return ChocoBuckets.isInfinity(stack) ? stack : new ItemStack(Items.BUCKET);
//            }
//
//            if(state.getBlock() instanceof AbstractCauldronBlock) {
//                return stack;
//            }
//
//            return WATER_BUCKET_DEFAULT.dispense(pointer, stack);
//        });
//
//        DispenserBehavior EMPTY_BUCKET_DEFAULT = BEHAVIORS.get(Items.BUCKET);
//        DispenserBlock.registerBehavior(Items.BUCKET, (pointer, stack) -> {
//            BlockPos pos;
//            BlockState state = pointer.world().getBlockState(pos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING)));
//            if (state.getBlock() == Blocks.LAVA_CAULDRON) {
//                pointer.world().setBlockState(pos, Blocks.CAULDRON.getDefaultState());
//                pointer.world().playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundCategory.BLOCKS, 1.0f, 1.0f);
//                pointer.world().emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
//
//                return ChocoBuckets.isInfinity(stack) ? stack : new ItemStack(Items.LAVA_BUCKET);
//            }
//
//            if(state.getBlock() == Blocks.WATER_CAULDRON && state.get(LeveledCauldronBlock.LEVEL) == 3) {
//                pointer.world().setBlockState(pos, Blocks.CAULDRON.getDefaultState());
//                pointer.world().playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
//                pointer.world().emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
//                return ChocoBuckets.isInfinity(stack) ? stack : new ItemStack(Items.WATER_BUCKET);
//            }
//
//            if(state.getBlock() instanceof AbstractCauldronBlock) {
//                return stack;
//            }
//            return EMPTY_BUCKET_DEFAULT.dispense(pointer, stack);
//        });
//
//        DispenserBehavior LAVA_BUCKET_DEFAULT = BEHAVIORS.get(Items.LAVA_BUCKET);
//        DispenserBlock.registerBehavior(Items.LAVA_BUCKET, (pointer, stack) -> {
//            BlockPos pos;
//            BlockState state = pointer.world().getBlockState(pos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING)));
//            if (state.getBlock() == Blocks.CAULDRON) {
//                pointer.world().setBlockState(pos, Blocks.LAVA_CAULDRON.getDefaultState());
//                pointer.world().playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY_LAVA, SoundCategory.BLOCKS, 1.0f, 1.0f);
//                pointer.world().emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
//
//                return new ItemStack(Items.BUCKET);
//            }
//
//            if(state.getBlock() instanceof AbstractCauldronBlock) {
//                return stack;
//            }
//            return LAVA_BUCKET_DEFAULT.dispense(pointer, stack);
//        });
//
//    }
//
//}
