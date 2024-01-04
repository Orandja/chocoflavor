package net.orandja.chocoflavor;

import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.orandja.chocoflavor.enchantment.EnchantmentArraySetting;
import net.orandja.chocoflavor.accessor.DispenserBlockAccessor;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.chocoflavor.utils.StackUtils;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.minecraft.world.event.GameEvent.FLUID_PICKUP;

public class ChocoBuckets {
    private ChocoBuckets() {}

    public static void init() {
        Map<Item, DispenserBehavior> BEHAVIORS = ((DispenserBlockAccessor)Blocks.DISPENSER).getBEHAVIORS();

        ChocoEnchantments.createRegistry(Items.BUCKET)
                .allowInAnvil((stack, enchantment) -> {
                    if(stack.getCount() == 1) {
                        if(ENABLING.contains(enchantment)) {
                            return true;
                        }

                        if(CAPACITY.contains(enchantment)) {
                            return ENABLING.anyMatch(it -> EnchantmentHelper.getLevel(it, stack) > 0) && stack.hasEnchantments();
                        }
                    }

                    return false;
                });

        ChocoEnchantments.createRegistry(Items.BUCKET)
                .allowInAnvil(Enchantments.INFINITY);

        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(Items.WATER_BUCKET, (state, world, pos, player, hand, stack) ->
                handleCauldron(true, world, pos, player, hand, stack, new ItemStack(Items.BUCKET), state, FILLED_WITH_WATER, SoundEvents.ITEM_BUCKET_FILL, null));
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(Items.WATER_BUCKET, (state, world, pos, player, hand, stack) ->
                handleCauldron(true, world, pos, player, hand, stack, new ItemStack(Items.BUCKET), state, FILLED_WITH_WATER, SoundEvents.ITEM_BUCKET_FILL, it -> !isCauldronFull(it)));

        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) ->
                handleCauldron(false, world, pos, player, hand, stack, new ItemStack(Items.WATER_BUCKET), state, DEFAULT_CAULDRON, SoundEvents.ITEM_BUCKET_EMPTY, ChocoBuckets::isCauldronFull));
        CauldronBehavior.LAVA_CAULDRON_BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) ->
                handleCauldron(false, world, pos, player, hand, stack, new ItemStack(Items.LAVA_BUCKET), state, DEFAULT_CAULDRON, SoundEvents.ITEM_BUCKET_EMPTY, ChocoBuckets::isCauldronFull));

        DispenserBehavior WATER_BUCKET_DEFAULT = BEHAVIORS.get(Items.WATER_BUCKET);
        DispenserBlock.registerBehavior(Items.WATER_BUCKET, (pointer, stack) -> {
            BlockPos pos;
            BlockState state = pointer.world().getBlockState(pos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING)));
            if (state.getBlock() == Blocks.CAULDRON) {
                pointer.world().setBlockState(pos, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3));
                pointer.world().playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                pointer.world().emitGameEvent(null, GameEvent.FLUID_PLACE, pos);

                return ChocoBuckets.isInfinity(stack) ? stack : new ItemStack(Items.BUCKET);
            }

            if(state.getBlock() instanceof AbstractCauldronBlock) {
                return stack;
            }

            return GlobalUtils.run(WATER_BUCKET_DEFAULT.dispense(pointer, stack), it -> isInfinity(stack) ? stack : it);
        });

        DispenserBehavior EMPTY_BUCKET_DEFAULT = BEHAVIORS.get(Items.BUCKET);
        DispenserBlock.registerBehavior(Items.BUCKET, (pointer, stack) -> {
            BlockPos pos;
            BlockState state = pointer.world().getBlockState(pos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING)));
            if (state.getBlock() == Blocks.LAVA_CAULDRON) {
                pointer.world().setBlockState(pos, Blocks.CAULDRON.getDefaultState());
                pointer.world().playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundCategory.BLOCKS, 1.0f, 1.0f);
                pointer.world().emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);

                return ChocoBuckets.isInfinity(stack) ? stack : new ItemStack(Items.LAVA_BUCKET);
            }

            if(state.getBlock() == Blocks.WATER_CAULDRON && state.get(LeveledCauldronBlock.LEVEL) == 3) {
                pointer.world().setBlockState(pos, Blocks.CAULDRON.getDefaultState());
                pointer.world().playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                pointer.world().emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
                return ChocoBuckets.isInfinity(stack) ? stack : new ItemStack(Items.WATER_BUCKET);
            }

            if(state.getBlock() instanceof AbstractCauldronBlock) {
                return stack;
            }
            return GlobalUtils.run(EMPTY_BUCKET_DEFAULT.dispense(pointer, stack), it -> isInfinity(stack) ? stack : it);
        });


        DispenserBehavior LAVA_BUCKET_DEFAULT = BEHAVIORS.get(Items.LAVA_BUCKET);
        DispenserBlock.registerBehavior(Items.LAVA_BUCKET, (pointer, stack) -> {
            BlockPos pos;
            BlockState state = pointer.world().getBlockState(pos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING)));
            if (state.getBlock() == Blocks.CAULDRON) {
                pointer.world().setBlockState(pos, Blocks.LAVA_CAULDRON.getDefaultState());
                pointer.world().playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY_LAVA, SoundCategory.BLOCKS, 1.0f, 1.0f);
                pointer.world().emitGameEvent(null, GameEvent.FLUID_PLACE, pos);

                return new ItemStack(Items.BUCKET);
            }

            if(state.getBlock() instanceof AbstractCauldronBlock) {
                return stack;
            }
            return LAVA_BUCKET_DEFAULT.dispense(pointer, stack);
        });
    }

    public static final EnchantmentArraySetting ENABLING = new EnchantmentArraySetting("infinitybucket.enabling.enchantments", new Enchantment[] { Enchantments.INFINITY });
    public static final EnchantmentArraySetting CAPACITY = new EnchantmentArraySetting("infinitybucket.capacity.enchantments", new Enchantment[] { Enchantments.EFFICIENCY });

    public static boolean isInfinity(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
    }

    public static final BlockState FILLED_WITH_WATER = Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3);
    public static final BlockState DEFAULT_CAULDRON = Blocks.CAULDRON.getDefaultState();
    public static final BlockState LAVA_CAULDRON = Blocks.LAVA_CAULDRON.getDefaultState();
    public static boolean isCauldronFull(BlockState state) {
        return state.getBlock() instanceof AbstractCauldronBlock block && block.isFull(state);
    }

    private static ItemStack getInfinityBucketOrCompute(ItemStack stack, PlayerEntity player, Supplier<ItemStack> supplier) {
        if(isInfinity(stack)) {
            if(player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0, StackUtils.getHandSlotForStack(player, stack), stack));
            }
            return stack;
        }

        return supplier.get();
    }

    public static ActionResult handleCauldron(boolean fill, World world, BlockPos pos, PlayerEntity player, Hand hand,
                                       ItemStack stack, ItemStack output, BlockState state, BlockState newState, SoundEvent soundEvent, Predicate<BlockState> predicate) {
        if(!fill && !predicate.test(state)) {
            return ActionResult.PASS;
        }

        if(!world.isClient) {
            player.setStackInHand(hand, handleInfinityBucket(stack, player, output));
            player.incrementStat(fill ? Stats.FILL_CAULDRON : Stats.USE_CAULDRON);
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            world.setBlockState(pos, fill ? newState : Blocks.CAULDRON.getDefaultState());
            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, fill ? GameEvent.FLUID_PLACE : FLUID_PICKUP, pos);
        }

        return ActionResult.success(world.isClient);
    }

    public static ItemStack handleInfinityBucket(ItemStack stack, PlayerEntity player) {
        return getInfinityBucketOrCompute(stack, player, () -> BucketItem.getEmptiedStack(stack, player));
    }

    public static ItemStack handleInfinityBucket(ItemStack stack, PlayerEntity player, ItemStack output) {
        return getInfinityBucketOrCompute(stack, player, () -> ItemUsage.exchangeStack(stack, player, output));
    }

    public interface Handler {
        default void onEmptying(World world, PlayerEntity user, Hand hand, Supplier<BlockHitResult> blockHitResultSupplier) {
            ItemStack itemStack = user.getStackInHand(hand);
            if(itemStack.hasNbt() && itemStack.getNbt().contains(ItemStack.ENCHANTMENTS_KEY)) {
                if(ENABLING.anyMatch(it -> EnchantmentHelper.getLevel(it, itemStack) > 0)) {
                    CAPACITY.computeWithValue(itemStack, level -> {
//                        BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
                        BlockHitResult blockHitResult = blockHitResultSupplier.get();
                        Direction direction = blockHitResult.getSide();
                        BlockPos blockPos2 = blockHitResult.getBlockPos().offset(direction);
                        for(int x = -level; x <= level; x++) {
                            for(int y = -level; y <= level; y++) {
                                for(int z = -level; z <= level; z++) {
                                    if(!(x == 0 && y == 0 && z == 0)) {
                                        BlockPos f = blockPos2.add(x, y, z);
                                        if (world.canPlayerModifyAt(user, f) && user.canPlaceOn(f, direction, itemStack)) {
                                            BlockState blockState = world.getBlockState(f);
                                            if (blockState.getBlock() instanceof FluidDrainable fluidDrainable) {
                                                fluidDrainable.tryDrainFluid(user, world, f, blockState);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }
    }
}
