package net.orandja.chocoflavor.tooltask;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.chocoflavor.utils.StackUtils;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class AdjascentToolTask extends DoubleToolTask {

    private static final int[][] DIRECTLY = new int[][] {
            /* DOWN */  { -1, 1, 0, 0, -1, 1 },
            /* UP */    { -1, 1, 0, 0, -1, 1 },
            /* NORTH */ { -1, 1, -1, 1, 0, 0 },
            /* SOUTH */ { -1, 1, -1, 1, 0, 0 },
            /* WEST */  { 0, 0, -1, 1, -1, 1 },
            /* EAST */  { 0, 0, -1, 1, -1, 1 }
    };

    private static final int[][] DIRECTLY_DOWN = new int[][] {
            /* DOWN */  { -1, 1, 0, 0, -1, 1 },
            /* UP */    { -1, 1, 0, 0, -1, 1 },
            /* NORTH */ { -1, 1, -2, 0, 0, 0 },
            /* SOUTH */ { -1, 1, -2, 0, 0, 0 },
            /* WEST */  { 0, 0, -2, 0, -1, 1 },
            /* EAST */  { 0, 0, -2, 0, -1, 1 }
    };

    public static final AdjascentToolTask All = new AdjascentToolTask("adjascent.all", DIRECTLY);

    public static final AdjascentToolTask SimilarOnly = new AdjascentToolTask("adjascent.similar", DIRECTLY) {
        @Override
        public boolean shouldBreak(Block blockA, Block blockB) {
            return blockA.equals(blockB);
        }
    };

    public static AdjascentToolTask Down = new AdjascentToolTask("directly.down.all", DIRECTLY_DOWN) {
        @Override
        public int getDirection(PlayerEntity player) {
            Direction[] directions = Direction.getEntityFacingOrder(player);
            for(int i = 0; i < 3; i++) {
                if(!GlobalUtils.anyEquals(directions[i], Direction.UP, Direction.DOWN)) {
                    return directions[i].getId();
                }
            }
            return Direction.DOWN.getId();
        }
    };

    public static AdjascentToolTask DownSimilarOnly = new AdjascentToolTask("directly.down.similar", DIRECTLY_DOWN) {
        @Override
        public boolean shouldBreak(Block blockA, Block blockB) {
            return blockA.equals(blockB);
        }

        @Override
        public int getDirection(PlayerEntity player) {
            Direction[] directions = Direction.getEntityFacingOrder(player);
            for(int i = 0; i < 3; i++) {
                if(!GlobalUtils.anyEquals(directions[i], Direction.UP, Direction.DOWN)) {
                    return directions[i].getId();
                }
            }
            return Direction.DOWN.getId();
        }
    };

    /**
     * Each array correspond to a Direction id {@link Direction#getId()}
     * Each sub array correspond to, in order, xMin, xMax, yMin, yMax, zMin, zMax
     * */
    private final int[][] zones;

    // Do not make protected.
    public AdjascentToolTask(String name, int[][] zones) {
        super(name);
        this.zones = zones;
    }

    @Override
    public void execute(World world, BlockPos pos, PlayerEntity player, BlockState state, ItemStack mainHand, ItemStack offHand, Consumer<BlockPos> consumer) {
        int[] zone = zones[getDirection(player)];

        Block block = state.getBlock();
        for(int y = zone[2]; y <= zone[3]; y++) {
            for(int x = zone[0]; x <= zone[1]; x++) {
                for(int z = zone[4]; z <= zone[5]; z++) {
                    BlockPos oPos = pos.add(x, y, z);
                    if(shouldBreak(block, world.getBlockState(oPos).getBlock()) && !StackUtils.anyGonnaBreak(mainHand, offHand)) {
                        consumer.accept(oPos);
                    }
                }
            }
        }
    }

    public int getDirection(PlayerEntity player) {
        return Direction.getEntityFacingOrder(player)[0].getId();
    }

    public boolean shouldBreak(Block blockA, Block blockB) {
        return true;
//        return blockA.equals(blockB);
    }
}
