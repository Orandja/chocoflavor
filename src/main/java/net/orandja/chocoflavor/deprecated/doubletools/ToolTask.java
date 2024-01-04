//package net.orandja.chocoflavor.mods.doubletools;
//
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.*;
//import net.minecraft.util.Pair;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.world.World;
//import net.orandja.chocoflavor.utils.BlockUtils;
//import net.orandja.chocoflavor.utils.StackUtils;
//
//import java.util.Map;
//import java.util.Optional;
//import java.util.function.Consumer;
//
//public class ToolTask {
//
//    public static ToolTask defaultFor(ItemStack stack) {
//        Optional<Map.Entry<Class<?>, ToolTask[]>> first = validModes.entrySet().stream().filter(it -> it.getKey().isAssignableFrom(stack.getItem().getClass())).findFirst();
//        return first.isPresent() ? first.get().getValue()[0] : TASK_ALL;
//    }
//
//    public static ToolTask[] tasksFor(ItemStack stack) {
//        Optional<Map.Entry<Class<?>, ToolTask[]>> first = validModes.entrySet().stream().filter(it -> it.getKey().isAssignableFrom(stack.getItem().getClass())).findFirst();
//        return first.isPresent() ? first.get().getValue() : new ToolTask[] {TASK_ALL};
//    }
//
//    public static boolean hasTasksFor(ItemStack stack) {
//        return validModes.entrySet().stream().anyMatch(it -> it.getKey().isAssignableFrom(stack.getItem().getClass()));
//    }
//    public interface ToolTaskExecutor {
//        void execute(World world, BlockPos pos, PlayerEntity player, BlockState state, Pair<ItemStack, ItemStack> stacksInHands, Consumer<BlockPos> consumer);
//    }
//    static BlockUtils.Zone AXE_ADJASCENTS_ZONE = new BlockUtils.Zone(-1, 1, 0, 1, -1, 1);
//    static BlockUtils.Zone SHEARS_ADJASCENTS_ZONE = new BlockUtils.Zone(-1, 1, -1, 1, -1, 1);
//    static BlockUtils.Zone PICKAXE_ADJASCENTS_ZONE = new BlockUtils.Zone(-1, 1, -1, 1, -1, 1);
//    static Map<Direction, BlockUtils.Zone> DIRECTION_ADJASCENTS_ZONE = Map.of(
//            Direction.EAST, new BlockUtils.Zone(0, 0, -1, 1, -1, 1),
//            Direction.WEST, new BlockUtils.Zone(0, 0, -1, 1, -1, 1),
//
//            Direction.UP, new BlockUtils.Zone(-1, 1, 0, 0, -1, 1),
//            Direction.DOWN, new BlockUtils.Zone(-1, 1, 0, 0, -1, 1),
//
//            Direction.NORTH, new BlockUtils.Zone(-1, 1, -1, 1, 0, 0),
//            Direction.SOUTH, new BlockUtils.Zone(-1, 1, -1, 1, 0, 0)
//    );
//
//    static BlockUtils.Zone HOE_SQUARE_ZONE = new BlockUtils.Zone(-2, 1, 0, 0, -2, 1);
//
//    static ToolTask TASK_SIMILAR = new ToolTask("Similar", (world, pos, player, state, stacksInHands, consumer) -> {
//        DIRECTION_ADJASCENTS_ZONE.get(Direction.getEntityFacingOrder(player)[0]).get(world, pos, 32, BlockUtils.Zone.NEVER_REPEAT, it -> state.getBlock().equals(it.getBlock())).forEach(it -> {
//            if(!StackUtils.anyGonnaBreak(stacksInHands)) {
//                consumer.accept(it);
//            }
//        });
//    });
//
//    static ToolTask TASK_ALL = new ToolTask("All", (world, pos, player, state, stacksInHands, consumer) -> {
//        DIRECTION_ADJASCENTS_ZONE.get(Direction.getEntityFacingOrder(player)[0]).get(world, pos, 32, BlockUtils.Zone.NEVER_REPEAT, stacksInHands.getLeft()::isSuitableFor).forEach(it -> {
//            if(!StackUtils.anyGonnaBreak(stacksInHands)) {
//                consumer.accept(it);
//            }
//        });
//    });
//
//    static ToolTask TASK_VEIN = new ToolTask("Vein Miner", (world, pos, player, state, stacksInHands, consumer) -> {
//        if(BlockUtils.isOre(state)) {
//            PICKAXE_ADJASCENTS_ZONE.get(world, pos, 32, it -> new Pair<>(BlockUtils.areSameType(world.getBlockState(it), state, BlockUtils::isOre), it), BlockUtils::isOre).forEach(it -> {
//                if(!StackUtils.anyGonnaBreak(stacksInHands)) {
//                    consumer.accept(it);
//                }
//            });
//        }
//    });
//
//    static ToolTask TASK_AXE = new ToolTask("Axe", (world, pos, player, state, stacksInHands, consumer) -> {
//        if(BlockUtils.isWood(state)) {
//            Block baseWood = state.getBlock();
//            AXE_ADJASCENTS_ZONE.get(world, pos, 128,
//                    it -> new Pair<>(BlockUtils.areSameWoods(world.getBlockState(it), baseWood), it),
//                    it -> BlockUtils.areSameWoods(it, baseWood)).forEach(it -> {
//                if(!StackUtils.anyGonnaBreak(stacksInHands)) {
//                    consumer.accept(it);
//                }
//            });
//        }
//    });
//
//    static ToolTask TASK_SHEARS = new ToolTask("Shears", (world, pos, player, state, stacksInHands, consumer) -> {
//        if(BlockUtils.isLeaves(state)) {
//            Block leaves = state.getBlock();
//            SHEARS_ADJASCENTS_ZONE.get(world, pos, 128,
//                    it -> new Pair<>(BlockUtils.areSameType(world.getBlockState(it), state, BlockUtils::isLeaves) || BlockUtils.isWood(world.getBlockState(it)), it),
//                    it -> BlockUtils.areSameType(it, state, BlockUtils::isLeaves)).forEach(it -> {
//                if(!StackUtils.anyGonnaBreak(stacksInHands)) {
//                    consumer.accept(it);
//                }
//            });
//        }
//    });
//
//    static ToolTask TASK_HOE_LINE = new ToolTask("Line", (world, pos, player, state, stacksInHands, consumer) -> {
//
//    });
//
//    protected static final Map<Class<?>, ToolTask[]> validModes = Map.of(
//            PickaxeItem.class, new ToolTask[] { TASK_ALL, TASK_SIMILAR, TASK_VEIN },
//            ShovelItem.class, new ToolTask[] { TASK_ALL, TASK_SIMILAR },
//            AxeItem.class, new ToolTask[] { TASK_AXE },
//            HoeItem.class, new ToolTask[] { TASK_ALL },
//            ShearsItem.class, new ToolTask[] { TASK_SHEARS }
//    );
//
//    private final ToolTaskExecutor executor;
//    private final String name;
//
//    public ToolTask(String name, ToolTaskExecutor executor) {
//        this.name = name;
//        this.executor = executor;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public ToolTaskExecutor getExecutor() {
//        return executor;
//    }
//
//
//}
