package net.orandja.chocoflavor;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.orandja.chocoflavor.tooltask.AdjascentToolTask;
import net.orandja.chocoflavor.tooltask.DoubleToolTask;
import net.orandja.chocoflavor.tooltask.VeinToolTask;
import net.orandja.chocoflavor.utils.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChocoDoubleTools {

    private ChocoDoubleTools() {}

    public static final DefaultedMap<Class<? extends Item>, DoubleToolTask> availableTasks = new DefaultedMap<>(GlobalUtils::isSubClass);
    public static void init() {
        availableTasks.add(ShovelItem.class, new DoubleToolTask[] { AdjascentToolTask.All, AdjascentToolTask.SimilarOnly, AdjascentToolTask.Down, AdjascentToolTask.DownSimilarOnly });
        availableTasks.add(PickaxeItem.class, new DoubleToolTask[] { AdjascentToolTask.All, AdjascentToolTask.SimilarOnly, AdjascentToolTask.Down, AdjascentToolTask.DownSimilarOnly, VeinToolTask.oreVeinMine });

        ChocoInventories.listenForMiddleClick(stack -> availableTasks.contains(stack.getItem().getClass()), (stack, player) -> {
            if(player instanceof UserHandler toolUser) {
                player.sendMessage(Text.of("Set tool mode to: " + toolUser.setNextToolTask(stack, availableTasks.getFor(stack.getItem().getClass())).getName()), true);
            }
        });
    }

    public interface HoeHandler {
        default void useDoubleHoe(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
            World world = context.getWorld();
            if(world.isClient) {
                return;
            }

            BlockPos pos = context.getBlockPos();
            PlayerEntity player = context.getPlayer();
            Hand hand = context.getHand();
            BlockState state = world.getBlockState(pos);

            if(state.getBlock() instanceof CropBlock block && block.isMature(state)) {
                if(!PlayerUtils.areBothTools(player, HoeItem.class)) {
                    world.setBlockState(pos, block.withAge(0), 2);
                    Block.dropStacks(state, world, pos, null, player, player.getStackInHand(hand));
                    context.getStack().damage(1, player, it -> it.sendToolBreakStatus(hand));

                    info.setReturnValue(ActionResult.SUCCESS);
                    return;
                }

                Direction direction = Direction.getEntityFacingOrder(player)[0];
                DropsList dropsList = new DropsList();
                new BlockNavigator(world, pos).navigate((navigator, blockPos, blockState) -> {
                    if(navigator.visitedCount() < 16 && Objects.nonNull(blockPos) && Objects.nonNull(blockState) &&
                        blockState.getBlock().equals(block) &&
                            PlayerUtils.anyToolBreaking(player)) {
                        if(block.isMature(blockState)) {
                            navigator.save(blockPos);
                            damageTools(player, blockState);
                        }
                        return direction;
                    }

                    return null;
                }).getSaved(blockPos -> {
                    world.setBlockState(blockPos, block.withAge(0), 2);
                    dropsList.addDrop(Block.getDroppedStacks(state, (ServerWorld) world, pos, null, player, player.getMainHandStack()));
                });

                dropsList.getDrops(stack -> {
                    Block.dropStack(world, pos, stack.copy());
                    ExperienceOrbEntity.spawn((ServerWorld) world, new Vec3d(pos.getX(), pos.getY(), pos.getZ()), stack.getCount() - 1);
                    state.onStacksDropped((ServerWorld) world, pos, player.getMainHandStack(), true);
                });

                info.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

    private static void damageTools(PlayerEntity player, BlockState state) {
        player.getMainHandStack().damage(1, player, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        player.getOffHandStack().damage(1, player, e -> e.sendEquipmentBreakStatus(EquipmentSlot.OFFHAND));

        player.incrementStat(Stats.MINED.getOrCreateStat(state.getBlock()));
        player.addExhaustion(0.005f);
    }

    private static void destroyBlock(PlayerEntity player, BlockState state, BlockPos pos) {
        damageTools(player, state);
        PlayerUtils.tryBreakBlock(player, pos);
    }

    public interface PickaxeHandler {
        default boolean useDoublePickaxes(boolean value, World world, BlockState state, BlockPos pos, LivingEntity entity) {
            if(!world.isClient && entity instanceof PlayerEntity player && player instanceof UserHandler toolUser && PlayerUtils.areBothToolsSuitable(player, state, PickaxeItem.class)) {
                toolUser.getToolTask(player.getMainHandStack()).execute(world, pos, player, state, player.getMainHandStack(), player.getOffHandStack(), it -> {
                    if(PlayerUtils.areBothToolsSuitable(player, world.getBlockState(it), PickaxeItem.class)) {
                        destroyBlock(player, state, it);
                    }
                });
            }

            return value;
        }
    }

    public interface ShovelHandler {
        default boolean useDoubleShovels(boolean value, World world, BlockState state, BlockPos pos, LivingEntity entity) {
            if(!world.isClient && entity instanceof PlayerEntity player && player instanceof UserHandler toolUser && PlayerUtils.areBothToolsSuitable(player, state, ShovelItem.class)) {
                toolUser.getToolTask(player.getMainHandStack()).execute(world, pos, player, state, player.getMainHandStack(), player.getOffHandStack(), it -> {
                    if(PlayerUtils.areBothToolsSuitable(player, world.getBlockState(it), ShovelItem.class)) {
                        destroyBlock(player, state, it);
                    }
                });
            }

            return value;
        }
    }

    public interface AxeHandler {
        default void useDoubleAxes(World world, BlockState state, BlockPos pos, LivingEntity entity, Runnable defaultMethod) {
            if(!world.isClient && BlockUtils.isWood(state) && entity instanceof PlayerEntity player && PlayerUtils.areBothToolsSuitable(player, state, AxeItem.class)) {
                if(VeinToolTask.woodVeinMine.execute(world, pos, player, state, player.getMainHandStack(), player.getOffHandStack(), it -> {
                    if(PlayerUtils.areBothToolsSuitable(player, world.getBlockState(it), ShovelItem.class)) {
                        destroyBlock(player, state, it);
                    }
                })) {
                    defaultMethod.run();
                }
            } else {
                defaultMethod.run();
            }
        }
    }

    public interface ShearsHandler {
        int[] zone = new int[]{ -1, 1, 0, 1, -1, 1 };
        default void useDoubleShears(World world, BlockState state, BlockPos pos, LivingEntity entity, Class<?> clazz) {
            if(!world.isClient && BlockUtils.isLeaves(state) && entity instanceof PlayerEntity player && PlayerUtils.areBothTools(player, clazz)) {
                new BlockNavigator(world, pos)
                        .onSaved(it -> destroyBlock(player, state, it))
                        .scanAtWithItself(pos, zone, (nav, oPos, oState, itself) -> {
                            if(StackUtils.anyGonnaBreak(player.getMainHandStack(), player.getOffHandStack())) {
                                return;
                            }

                            if(BlockUtils.areSameType(oState, state, BlockUtils::isLeaves)) {
                                nav.save(oPos);
                                itself.accept(oPos);
                            } else if(BlockUtils.isWood(oState)) {
                                itself.accept(oPos);
                            }
                        });
            }
        }
    }

    public interface UserHandler {
        HashMap<Item, DoubleToolTask> getTasks();
        void setToolTasks(HashMap<Item, DoubleToolTask> toolTasks);
        default DoubleToolTask getToolTask(ItemStack stack) {
            return getTasks().getOrDefault(stack.getItem(), availableTasks.getFirst(stack.getItem().getClass()));
        }

        default DoubleToolTask setToolTask(ItemStack stack, DoubleToolTask toolTask) {
            getTasks().put(stack.getItem(), toolTask);
            return toolTask;
        }

        default DoubleToolTask setNextToolTask(ItemStack stack, DoubleToolTask[] toolTasks) {
            return setToolTask(stack, toolTasks[MathUtils.nextIndexOf(toolTasks, getToolTask(stack))]);
        }
    }
}
