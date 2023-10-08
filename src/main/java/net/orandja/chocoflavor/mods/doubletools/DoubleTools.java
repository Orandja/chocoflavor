package net.orandja.chocoflavor.mods.doubletools;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.orandja.chocoflavor.ChocoFlavor;
import net.orandja.chocoflavor.mods.core.InventoryInteract;
import net.orandja.chocoflavor.utils.BlockUtils;
import net.orandja.chocoflavor.utils.MathUtils;
import net.orandja.chocoflavor.utils.PlayerUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public interface DoubleTools {

    interface ToolUser {
        HashMap<Item, ToolTask> getToolTasks();
        void setToolTasks(HashMap<Item, ToolTask> toolTasks);
        default ToolTask getToolTask(ItemStack stack) {
            return getToolTasks().getOrDefault(stack.getItem(), ToolTask.defaultFor(stack));
        }
        default ToolTask setToolTask(ItemStack stack, ToolTask toolTask) {
            getToolTasks().put(stack.getItem(), toolTask);
            return toolTask;
        }

        default ToolTask setNextToolTask(ItemStack stack, ToolTask[] toolTasks) {
            return setToolTask(stack, toolTasks[MathUtils.nextIndexOf(toolTasks, getToolTask(stack))]);
        }

    }

    interface AfterBreakBlock {
        void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack);
    }

    default boolean usePickaxe(boolean value, ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entity, Class<?> clazz) {
        if(!world.isClient && entity instanceof PlayerEntity player && player instanceof ToolUser toolUser && PlayerUtils.areBothToolsSuitable(player, state, clazz)) {
            toolUser.getToolTask(player.getMainHandStack()).getExecutor().execute(world, pos, player, state, PlayerUtils.getBothTools(player), it -> {
                player.getMainHandStack().damage(1, player, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
                player.getOffHandStack().damage(1, player, e -> e.sendEquipmentBreakStatus(EquipmentSlot.OFFHAND));

                player.incrementStat(Stats.MINED.getOrCreateStat(state.getBlock()));
                player.addExhaustion(0.005f);
                PlayerUtils.tryBreakBlock(player, it);
            });
        }

        return value;
    }

    default void useAxe(Block block, BlockEntity blockEntity, World world, PlayerEntity player, BlockPos pos, BlockState state, ItemStack stack, AfterBreakBlock superAfterBreakBlock) {
        if(!world.isClient && BlockUtils.isWood(state) && PlayerUtils.areBothToolsSuitable(player, state, AxeItem.class)) {
            player.incrementStat(Stats.MINED.getOrCreateStat(block));
            player.addExhaustion(0.005f);

            AtomicInteger count = new AtomicInteger();
            ToolTask.TASK_AXE.getExecutor().execute(world, pos, player, state, PlayerUtils.getBothTools(player), it -> {
                player.getMainHandStack().damage(1, player, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
                player.getOffHandStack().damage(1, player, e -> e.sendEquipmentBreakStatus(EquipmentSlot.OFFHAND));

                player.incrementStat(Stats.MINED.getOrCreateStat(state.getBlock()));
                player.addExhaustion(0.005f);
                world.setBlockState(it, Blocks.AIR.getDefaultState(), 2);
                if(count.incrementAndGet() >= 64) {
                    dropStacks(blockEntity, world, player, pos, state, stack, count.get());
                    count.set(0);
                }
            });


            dropStacks(blockEntity, world, player, pos, state, stack, count.get());
            return;
        }

        // default afterBreak() execution
        superAfterBreakBlock.afterBreak(world, player, pos, state, blockEntity, stack);
    }

    default void useHoe(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        World world = context.getWorld();
        if(world.isClient) {
            return;
        }

        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        BlockState state = world.getBlockState(pos);
        Block block = world.getBlockState(pos).getBlock();

        if(!(block instanceof Fertilizable)) {
            return;
        }

        if(block instanceof CropBlock cropBlock && cropBlock.isMature(state)) {
            if(!PlayerUtils.areBothTools(player, HoeItem.class)) {
                world.setBlockState(pos, cropBlock.withAge(0), 2);
                Block.dropStacks(state, world, pos, null, player, player.getStackInHand(hand));
                context.getStack().damage(1, player, it -> it.sendToolBreakStatus(hand));

                info.setReturnValue(ActionResult.SUCCESS);
                return;
            }

            Direction direction = Direction.getEntityFacingOrder(player)[0];
            int i = 0;
            BlockPos nPos = null;
            BlockState nState = null;
            List<BlockPos> toRemove = Lists.newArrayList();
            ItemStack mainTool = player.getMainHandStack();
            ItemStack offTool = player.getOffHandStack();
            while(i < 16 && (nState = world.getBlockState(nPos = pos.offset(direction, i))).getBlock().equals(cropBlock)) {
                if(PlayerUtils.anyToolBreaking(player)) {
                    break;
                }

                if(((CropBlock) block).isMature(nState)) {
                    toRemove.add(nPos);
                    player.getMainHandStack().damage(1, player, it -> it.sendToolBreakStatus(Hand.MAIN_HAND));
                    player.getOffHandStack().damage(1, player, it -> it.sendToolBreakStatus(Hand.OFF_HAND));
                }
                i++;
            }
            Map<Item, ItemStack> dropMap = Maps.newHashMap();
            toRemove.forEach(it -> {
                world.setBlockState(it, cropBlock.withAge(0), 2);
                Block.getDroppedStacks(state, (ServerWorld) world, pos, null, player, mainTool).forEach(stack -> {
                    if(dropMap.containsKey(stack.getItem())) {
                        ItemStack otherStack = dropMap.get(stack.getItem());
                        int count = otherStack.getCount() + stack.getCount();
                        if(count > otherStack.getMaxCount()) {
                            otherStack.setCount(otherStack.getMaxCount());
                            Block.dropStack(world, pos, otherStack.copy());
                            count -= otherStack.getMaxCount();
                        }
                        stack.setCount(count);
                    }
                    dropMap.put(stack.getItem(), stack);
                });
            });

            if(toRemove.size() > 0) {
                ExperienceOrbEntity.spawn((ServerWorld) world, new Vec3d(pos.getX(), pos.getY(), pos.getZ()), toRemove.size() - 1);
                dropMap.values().forEach(it -> Block.dropStack(world, pos, it));
                state.onStacksDropped((ServerWorld) world, pos, mainTool, true);
            }

            info.setReturnValue(ActionResult.SUCCESS);
            return;
        }

        if(block instanceof CocoaBlock cocoaBlock) {
            if(state.get(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE) {
                world.setBlockState(pos, state.with(CocoaBlock.AGE, 0), 2);
                Block.dropStacks(state, world, pos, null, player, player.getStackInHand(hand));
                info.setReturnValue(ActionResult.SUCCESS);
                return;
            }
        }
    }

    static void beforeLaunch() {
        InventoryInteract.onMiddleClick.add(new Pair<>(ToolTask::hasTasksFor, (stack, player) -> {
            if(player instanceof ToolUser toolUser) {
                player.sendMessage(Text.of("Set tool mode to: " + toolUser.setNextToolTask(stack, ToolTask.tasksFor(stack)).getName()), true);
            }
        }));
    }

    static void dropStacks(BlockEntity blockEntity, World world, PlayerEntity player, BlockPos pos, BlockState state, ItemStack stack, int count) {
        if(world instanceof ServerWorld serverWorld) {
            Block.getDroppedStacks(state, serverWorld, pos, blockEntity, player, stack).forEach(it -> {
                it.increment(count);
                Block.dropStack(world, pos, it);
            });
            state.onStacksDropped(serverWorld, pos, stack, true);
        }
    }
    
}
