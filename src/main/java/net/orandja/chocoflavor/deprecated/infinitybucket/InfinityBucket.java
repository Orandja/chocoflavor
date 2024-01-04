//package net.orandja.chocoflavor.mods.infinitybucket;
//
//import net.minecraft.block.*;
//import net.minecraft.block.cauldron.CauldronBehavior;
//import net.minecraft.block.dispenser.DispenserBehavior;
//import net.minecraft.enchantment.Enchantment;
//import net.minecraft.enchantment.EnchantmentHelper;
//import net.minecraft.enchantment.Enchantments;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.item.*;
//import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.sound.SoundCategory;
//import net.minecraft.sound.SoundEvent;
//import net.minecraft.sound.SoundEvents;
//import net.minecraft.stat.Stats;
//import net.minecraft.util.ActionResult;
//import net.minecraft.util.Hand;
//import net.minecraft.util.math.BlockPointer;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import net.minecraft.world.event.GameEvent;
//import net.orandja.chocoflavor.mods.core.BlockWithEnchantment;
//import net.orandja.chocoflavor.mods.core.EnchantMore;
//import net.orandja.chocoflavor.mods.core.accessor.DispenserBlockAccessor;
//import net.orandja.chocoflavor.utils.GlobalUtils;
//
//import java.util.Map;
//import java.util.function.Predicate;
//import java.util.function.Supplier;
//
//import static net.minecraft.world.event.GameEvent.FLUID_PICKUP;
//
//public interface InfinityBucket {
//
//    static int getHandSlotForStack(PlayerEntity player, ItemStack stack) {
//        return player.getMainHandStack().equals(stack) ? player.getInventory().selectedSlot : PlayerInventory.OFF_HAND_SLOT;
//    }
//
//    static boolean isInfinity(ItemStack stack) {
//        return EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
//    }
//
//    BlockState FILLED_WITH_WATER = Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3);
//    BlockState DEFAULT_CAULDRON = Blocks.CAULDRON.getDefaultState();
//    BlockState LAVA_CAULDRON = Blocks.LAVA_CAULDRON.getDefaultState();
//    static boolean isCauldronFull(BlockState state) {
//        return state.getBlock() instanceof AbstractCauldronBlock block && block.isFull(state);
//    }
//
//    BlockWithEnchantment.EnchantmentArraySetting ENABLING = new BlockWithEnchantment.EnchantmentArraySetting("infinitybucket.enabling.enchantments", new Enchantment[] { Enchantments.INFINITY });
//    BlockWithEnchantment.EnchantmentArraySetting CAPACITY = new BlockWithEnchantment.EnchantmentArraySetting("infinitybucket.capacity.enchantments", new Enchantment[] { Enchantments.EFFICIENCY });
//    static void beforeLaunch() {
//        Map<Item, DispenserBehavior> BEHAVIORS = ((DispenserBlockAccessor)Blocks.DISPENSER).getBEHAVIORS();
//
//        EnchantMore.addComplex(Items.BUCKET, (enchantment, stack) -> {
//            if(stack.getCount() == 1) {
//                if(ENABLING.contains(enchantment)) {
//                    return true;
//                }
//
//                if(CAPACITY.contains(enchantment)) {
//                    return ENABLING.anyMatch(it -> EnchantmentHelper.getLevel(it, stack) > 0) && stack.hasEnchantments();
//                }
//            }
//
//            return false;
//        });
//        EnchantMore.addBasic(Items.WATER_BUCKET, Enchantments.INFINITY);
//
//        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(Items.WATER_BUCKET, (state, world, pos, player, hand, stack) ->
//            handleCauldron(true, world, pos, player, hand, stack, new ItemStack(Items.BUCKET), state, FILLED_WITH_WATER, SoundEvents.ITEM_BUCKET_FILL, null));
//        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(Items.WATER_BUCKET, (state, world, pos, player, hand, stack) ->
//            handleCauldron(true, world, pos, player, hand, stack, new ItemStack(Items.BUCKET), state, FILLED_WITH_WATER, SoundEvents.ITEM_BUCKET_FILL, it -> !InfinityBucket.isCauldronFull(it)));
//
//        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) ->
//            handleCauldron(false, world, pos, player, hand, stack, new ItemStack(Items.WATER_BUCKET), state, DEFAULT_CAULDRON, SoundEvents.ITEM_BUCKET_EMPTY, InfinityBucket::isCauldronFull));
//        CauldronBehavior.LAVA_CAULDRON_BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) ->
//            handleCauldron(false, world, pos, player, hand, stack, new ItemStack(Items.LAVA_BUCKET), state, DEFAULT_CAULDRON, SoundEvents.ITEM_BUCKET_EMPTY, InfinityBucket::isCauldronFull));
//
//        DispenserBehavior WATER_BUCKET_DEFAULT = BEHAVIORS.get(Items.WATER_BUCKET);
//        DispenserBlock.registerBehavior(Items.WATER_BUCKET, (BlockPointer pointer, ItemStack stack) -> GlobalUtils.run(WATER_BUCKET_DEFAULT.dispense(pointer, stack), it -> isInfinity(stack) ? stack : it));
//
//        DispenserBehavior EMPTY_BUCKET_DEFAULT = BEHAVIORS.get(Items.BUCKET);
//        DispenserBlock.registerBehavior(Items.BUCKET, (pointer, stack) -> GlobalUtils.run(EMPTY_BUCKET_DEFAULT.dispense(pointer, stack), it -> isInfinity(stack) ? stack : it));
//    }
//
//    private static ItemStack getInfinityBucketOrCompute(ItemStack stack, PlayerEntity player, Supplier<ItemStack> supplier) {
//        if(isInfinity(stack)) {
//            if(player instanceof ServerPlayerEntity serverPlayer) {
//                serverPlayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0, getHandSlotForStack(player, stack), stack));
//            }
//            return stack;
//        }
//
//        return supplier.get();
//    }
//
//    static ActionResult handleCauldron(boolean fill, World world, BlockPos pos, PlayerEntity player, Hand hand,
//                                       ItemStack stack, ItemStack output, BlockState state, BlockState newState, SoundEvent soundEvent, Predicate<BlockState> predicate) {
//        if(!fill && !predicate.test(state)) {
//            return ActionResult.PASS;
//        }
//
//        if(!world.isClient) {
//            player.setStackInHand(hand, handleInfinityBucket(stack, player, output));
//            player.incrementStat(fill ? Stats.FILL_CAULDRON : Stats.USE_CAULDRON);
//            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
//            world.setBlockState(pos, fill ? newState : Blocks.CAULDRON.getDefaultState());
//            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
//            world.emitGameEvent(null, fill ? GameEvent.FLUID_PLACE : FLUID_PICKUP, pos);
//        }
//
//        return ActionResult.success(world.isClient);
//    }
//
//    static ItemStack handleInfinityBucket(ItemStack stack, PlayerEntity player) {
//        return getInfinityBucketOrCompute(stack, player, () -> BucketItem.getEmptiedStack(stack, player));
//    }
//
//    static ItemStack handleInfinityBucket(ItemStack stack, PlayerEntity player, ItemStack output) {
//        return getInfinityBucketOrCompute(stack, player, () -> ItemUsage.exchangeStack(stack, player, output));
//    }
//}
