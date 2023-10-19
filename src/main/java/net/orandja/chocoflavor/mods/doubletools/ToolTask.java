package net.orandja.chocoflavor.mods.doubletools;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.orandja.chocoflavor.utils.BlockUtils;
import net.orandja.chocoflavor.utils.BlockZone;
import net.orandja.chocoflavor.utils.StackUtils;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class ToolTask {

    public static ToolTask defaultFor(ItemStack stack) {
        Optional<Map.Entry<Class<?>, ToolTask[]>> first = validModes.entrySet().stream().filter(it -> stack.getItem().getClass().isAssignableFrom(it.getKey())).findFirst();
        return first.isPresent() ? first.get().getValue()[0] : TASK_ALL;
    }

    public static ToolTask[] tasksFor(ItemStack stack) {
        Optional<Map.Entry<Class<?>, ToolTask[]>> first = validModes.entrySet().stream().filter(it -> stack.getItem().getClass().isAssignableFrom(it.getKey())).findFirst();
        return first.isPresent() ? first.get().getValue() : new ToolTask[] {TASK_ALL};
    }

    public static boolean hasTasksFor(ItemStack stack) {
        return validModes.entrySet().stream().anyMatch(it -> stack.getItem().getClass().isAssignableFrom(it.getKey()));
    }
    public interface ToolTaskExecutor {
        void execute(World world, BlockPos pos, PlayerEntity player, BlockState state, Pair<ItemStack, ItemStack> stacksInHands, Consumer<BlockPos> consumer);
    }
    static BlockZone AXE_ADJASCENTS_ZONE = new BlockZone(-1, 1, 0, 1, -1, 1);
    static BlockZone PICKAXE_ADJASCENTS_ZONE = new BlockZone(-1, 1, -1, 1, -1, 1);
    static Map<Direction, BlockZone> DIRECTION_ADJASCENTS_ZONE = Map.of(
            Direction.EAST, new BlockZone(0, 0, -1, 1, -1, 1),
            Direction.WEST, new BlockZone(0, 0, -1, 1, -1, 1),

            Direction.UP, new BlockZone(-1, 1, 0, 0, -1, 1),
            Direction.DOWN, new BlockZone(-1, 1, 0, 0, -1, 1),

            Direction.NORTH, new BlockZone(-1, 1, -1, 1, 0, 0),
            Direction.SOUTH, new BlockZone(-1, 1, -1, 1, 0, 0)
    );

    static BlockZone HOE_SQUARE_ZONE = new BlockZone(-2, 1, 0, 0, -2, 1);

    static ToolTask TASK_SIMILAR = new ToolTask("Similar", (world, pos, player, state, stacksInHands, consumer) -> {
        DIRECTION_ADJASCENTS_ZONE.get(Direction.getEntityFacingOrder(player)[0]).get(world, pos, 32, BlockZone.NEVER_REPEAT, it -> state.getBlock().equals(it.getBlock())).forEach(it -> {
            if(!StackUtils.anyGonnaBreak(stacksInHands)) {
                consumer.accept(it);
            }
        });
    });

    static ToolTask TASK_ALL = new ToolTask("All", (world, pos, player, state, stacksInHands, consumer) -> {
        DIRECTION_ADJASCENTS_ZONE.get(Direction.getEntityFacingOrder(player)[0]).get(world, pos, 32, BlockZone.NEVER_REPEAT, stacksInHands.getLeft()::isSuitableFor).forEach(it -> {
            if(!StackUtils.anyGonnaBreak(stacksInHands)) {
                consumer.accept(it);
            }
        });
    });

    static ToolTask TASK_VEIN = new ToolTask("Vein Miner", (world, pos, player, state, stacksInHands, consumer) -> {
        if(BlockUtils.isOre(state)) {
            PICKAXE_ADJASCENTS_ZONE.get(world, pos, 32, it -> new Pair<>(BlockUtils.isOre(world.getBlockState(it)), it), BlockUtils::isOre).forEach(it -> {
                if(!StackUtils.anyGonnaBreak(stacksInHands)) {
                    consumer.accept(it);
                }
            });
        }
    });

    static ToolTask TASK_AXE = new ToolTask("Axe", (world, pos, player, state, stacksInHands, consumer) -> {
        if(BlockUtils.isWood(state)) {
            AXE_ADJASCENTS_ZONE.get(world, pos, 128,
                    it -> new Pair<>(BlockUtils.isWood(world.getBlockState(it)) && state.getBlock().equals(world.getBlockState(it).getBlock()), it),
                    it -> BlockUtils.isWood(it) && state.getBlock().equals(it.getBlock())).forEach(it -> {
                if(!StackUtils.anyGonnaBreak(stacksInHands)) {
                    consumer.accept(it);
                }
            });
        }
    });

    static ToolTask TASK_HOE_LINE = new ToolTask("Line", (world, pos, player, state, stacksInHands, consumer) -> {

    });

    protected static final Map<Class<?>, ToolTask[]> validModes = Map.of(
            PickaxeItem.class, new ToolTask[] { TASK_ALL, TASK_SIMILAR, TASK_VEIN },
            ShovelItem.class, new ToolTask[] { TASK_ALL, TASK_SIMILAR },
            AxeItem.class, new ToolTask[] { TASK_AXE },
            HoeItem.class, new ToolTask[] { TASK_ALL }
    );

    private final ToolTaskExecutor executor;
    private final String name;

    public ToolTask(String name, ToolTaskExecutor executor) {
        this.name = name;
        this.executor = executor;
    }

    public String getName() {
        return name;
    }

    public ToolTaskExecutor getExecutor() {
        return executor;
    }


}
