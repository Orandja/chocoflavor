package net.orandja.chocoflavor.tooltask;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.utils.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class VeinToolTask extends DoubleToolTask {

    public static final VeinToolTask woodVeinMine = new VeinToolTask("veinminer.wood.task", new int[]{ -1, 1, 0, 1, -1, 1 }, (oState, baseWood) -> {
        if(BlockUtils.isLeavesIgnorePersistent(oState)) {
            if(oState.get(LeavesBlock.PERSISTENT)) {
                return BlockNavigator.State.CANCEL;
            }
            return BlockNavigator.State.ACCEPT;
        }

        if(BlockUtils.areSameWoods(oState, baseWood)) {
            return BlockNavigator.State.ACCEPT_AND_SAVE;
        }

        return BlockNavigator.State.PASS;
    });

    public static final VeinToolTask oreVeinMine = new VeinToolTask("veinminer.ore.task", false, new int[]{ -1, 1, -1, 1, -1, 1 }, BlockUtils::isOre, (oState, baseOre) -> {
        if(BlockUtils.isOre(oState) && oState.getBlock().equals(baseOre)) {
            return BlockNavigator.State.ACCEPT_AND_SAVE;
        }

        return BlockNavigator.State.PASS;
    });

    private final BiFunction<BlockState, Block, BlockNavigator.State> navigatorState;
    private final int[] zone;
    private final Predicate<BlockState> basePredicate;
    private final boolean breakFirstBlock;

    public VeinToolTask(String name, boolean breakFirstBlock, int[] zone, Predicate<BlockState> basePredicate, BiFunction<BlockState, Block, BlockNavigator.State> navigatorState) {
        super(name);
        this.zone = zone;
        this.basePredicate = basePredicate;
        this.navigatorState = navigatorState;
        this.breakFirstBlock = breakFirstBlock;
    }

    public VeinToolTask(String name, int[] zone, BiFunction<BlockState, Block, BlockNavigator.State> navigatorState) {
        this(name, true, zone, null, navigatorState);
    }

    @Override
    public boolean execute(World world, BlockPos pos, PlayerEntity player, BlockState state, ItemStack mainHand, ItemStack offHand, Consumer<BlockPos> consumer) {
        if(basePredicate != null && !basePredicate.test(state)) {
            return true;
        }

        Block baseBlock = state.getBlock();
        DropsList dropsList = new DropsList();

        AtomicBoolean cancelled = new AtomicBoolean(false);

        new BlockNavigator(world, pos)
                .scanAtWithItself(pos, zone, (nav, oPos, oState, itself) -> {
                    if(!cancelled.get()) {
                        switch(navigatorState.apply(oState, baseBlock)) {
                            case SAVE -> nav.save(oPos);
                            case ACCEPT -> itself.accept(oPos);
                            case ACCEPT_AND_SAVE -> {
                                nav.save(oPos);
                                itself.accept(oPos);
                            }
                            case CANCEL -> cancelled.set(true);
                        }
                    }
                }).getSaved(oPos -> {
                    if(cancelled.get() || StackUtils.anyGonnaBreak(player.getMainHandStack(), player.getOffHandStack())) {
                        return;
                    }
                    damageTools(player, state);
                    dropsList.addDrop(Block.getDroppedStacks(state, (ServerWorld) world, pos, null, player, player.getMainHandStack()));
                    world.setBlockState(oPos, Blocks.AIR.getDefaultState(), 2);
                });

        if(!cancelled.get()) {
            damageTools(player, state);
            if(breakFirstBlock) {
                dropsList.addDrop(Block.getDroppedStacks(state, (ServerWorld) world, pos, null, player, player.getMainHandStack()));
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
            }

            dropsList.getDrops(stack -> {
                Block.dropStack(world, pos, stack.copy());
                state.onStacksDropped((ServerWorld) world, pos, player.getMainHandStack(), true);
            });
        }
        return cancelled.get();
    }
}
