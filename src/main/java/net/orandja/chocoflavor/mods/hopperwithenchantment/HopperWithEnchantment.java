package net.orandja.chocoflavor.mods.hopperwithenchantment;

import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.orandja.chocoflavor.mods.core.BlockWithEnchantment;
import net.orandja.chocoflavor.mods.core.EnchantMore;
import net.orandja.chocoflavor.utils.StackUtils;

import java.util.stream.IntStream;

public interface HopperWithEnchantment extends BlockWithEnchantment {


//    Enchantment[] SPEED = new Enchantment[] { Enchantments.EFFICIENCY };
    EnchantmentArraySetting SPEED = new EnchantmentArraySetting("hopper.speed.enchantments", new Enchantment[] { Enchantments.EFFICIENCY });
//    Enchantment[] FILTER = new Enchantment[] { Enchantments.SILK_TOUCH };
    EnchantmentArraySetting FILTER = new EnchantmentArraySetting("hopper.filter.enchantments", new Enchantment[] { Enchantments.SILK_TOUCH});
//    Enchantment[] OUTPUT = new Enchantment[] { Enchantments.MENDING };
    EnchantmentArraySetting OUTPUT = new EnchantmentArraySetting("hopper.output.enchantments", new Enchantment[] { Enchantments.MENDING });
//    Enchantment[] OFFSET = new Enchantment[] { Enchantments.KNOCKBACK };
    EnchantmentArraySetting OFFSET = new EnchantmentArraySetting("hopper.offset.enchantments", new Enchantment[] { Enchantments.KNOCKBACK });
//    Enchantment[] RANGE = new Enchantment[] { Enchantments.SWEEPING };
    EnchantmentArraySetting RANGE = new EnchantmentArraySetting("hopper.range.enchantments", new Enchantment[] { Enchantments.SWEEPING });
    Enchantment[] VALID_ENCHANTMENTS = BlockWithEnchantment.concat(SPEED.getValue(), FILTER.getValue(), OUTPUT.getValue(), OFFSET.getValue(), RANGE.getValue());

    static void beforeLaunch() {
        EnchantMore.addBasic(Items.HOPPER, VALID_ENCHANTMENTS);
    }

//    default boolean FILTER_COMPUTE(ItemStack stack) {
//        return stack.isEmpty() || (this.getEnchantmentDictionary().hasAnyEnchantment(FILTER) && stack.getCount() == 1);
//    }
//
//    static IntPredicate FILTER_COMPUTE(Inventory inventory) {
//        if(inventory instanceof HopperWithEnchantment hopperWithEnchantment) {
//            return it -> hopperWithEnchantment.FILTER_COMPUTE(inventory.getStack(it));
//        }
//        return it -> inventory.getStack(it).isEmpty();
//    }

//    static void reduceCooldown(HopperBlockEntity hopper) {
//        BlockWithEnchantment.computeWithValue(hopper, HopperWithEnchantment.class, SPEED, (it, lvl) -> {
//            it.setHopperCooldown(Math.max(1, (int)(8 - (1.2D * lvl))));
//        });
//    }

//    static boolean filterIsInventoryEmpty(Inventory inventory, ItemStack[] stacks) {
//        return BlockWithEnchantment.getValue(inventory, HopperWithEnchantment.class, FILTER, (it, lvl) -> Arrays.stream(stacks).allMatch(stack -> stack.isEmpty() || stack.getCount() == 1), Arrays.stream(stacks).allMatch(ItemStack::isEmpty));
//    }

//    static IntStream filterGetAvailableSlots(Inventory inventory, Direction facing) {
//        return (inventory instanceof SidedInventory sidedInventory ?
//                IntStream.of(sidedInventory.getAvailableSlots(facing)) :
//                IntStream.range(0, inventory.size()))
//            .filter(it -> BlockWithEnchantment.getValue(inventory, HopperWithEnchantment.class, FILTER, (hopper, lvl) -> !inventory.getStack(it).isEmpty() && inventory.getStack(it).getCount() > 1, !inventory.getStack(it).isEmpty()));
//    }

//    static Comparable<?> redstoneEnabled(World world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, BooleanSupplier booleanSupplier) {
//        if(world.getBlockEntity(pos) instanceof HopperWithEnchantment hopperWithEnchantment) {
//            if(hopperWithEnchantment.getEnchantmentDictionary().hasAnyEnchantment(OUTPUT)) {
//                if(!state.get(HopperBlock.ENABLED)) {
//                    boolean bl = false;
//                    if(!hopperWithEnchantment.isHopperEmpty()) {
//                        bl = mendingRedstone(world, pos, state, blockEntity);
//                    }
//                    if(!hopperWithEnchantment.isHopperFull()) {
//                        bl = bl | booleanSupplier.getAsBoolean();
//                    }
//
//                    if(bl) {
//                        reduceCooldown(blockEntity);
//                        hopperWithEnchantment.markHopperDirty(world, pos, state);
//                    }
//                }
//                return false;
//            } else if(hopperWithEnchantment.getEnchantmentDictionary().hasAnyEnchantment(FILTER) && state.get(HopperBlock.ENABLED)) {
//                boolean bl = false;
//                if (!hopperWithEnchantment.isHopperEmpty()) {
//                    bl = filterInsert(world, pos, state, blockEntity);
//                }
//                if (!hopperWithEnchantment.isHopperFull()) {
//                    bl = bl | booleanSupplier.getAsBoolean();
//                }
//                if (bl) {
//                    reduceCooldown(blockEntity);
//                    hopperWithEnchantment.markHopperDirty(world, pos, state);
//                }
//                return false;
//            }
//        }
//        return state.get(HopperBlock.ENABLED);
//    }

    static boolean filterInsert(World world, BlockPos pos, BlockState state, Inventory inventory) {
        if(world.getBlockEntity(pos) instanceof HopperWithEnchantment hopperWithEnchantment) {
            Inventory outputInventory = hopperWithEnchantment.vw$getOutInventory(world, pos, state);
            if(outputInventory == null) {
                return false;
            }

            Direction direction = state.get(HopperBlock.FACING).getOpposite();
            if(hopperWithEnchantment.vw$isInvFull(outputInventory, direction)) {
                for(int i = 0; i < inventory.size(); i++) {
                    ItemStack stackFrom = inventory.getStack(i);
                    if(stackFrom.isEmpty() || stackFrom.getCount() == 1) {
                        continue;
                    }

                    ItemStack itemStack = stackFrom.copy();
                    ItemStack itemStack2 = HopperBlockEntity.transfer(inventory, outputInventory, inventory.removeStack(i, 1), direction);
                    if(itemStack2.isEmpty()) {
                        outputInventory.markDirty();
                        return true;
                    }
                    inventory.setStack(i, itemStack);
                }
            }
        }
        return false;
    }

    static boolean mendingRedstone(World world, BlockPos pos, BlockState state, Inventory inventory) {
        if(world.getBlockEntity(pos) instanceof HopperWithEnchantment hopperWithEnchantment) {
            Inventory outputInventory = hopperWithEnchantment.vw$getOutInventory(world, pos, state);
            int power = world.getReceivedRedstonePower(pos) - 1;
            if(power < 0 || outputInventory == null || power >= outputInventory.size()) {
                return false;
            }

            ItemStack outputStack = outputInventory.getStack(power);
            for(int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                if(stack.isEmpty() || stack.getCount() == 1) {
                    continue;
                }

                if(outputStack.isEmpty()) {
                    outputInventory.setStack(power, Inventories.splitStack(hopperWithEnchantment.vw$getInvList(), i, 1));
                    return true;
                }

                if (StackUtils.canMerge(outputStack, stack, 1)) {
                    stack.decrement(1);
                    outputStack.increment(1);
                    inventory.setStack(i, stack);
                    outputInventory.setStack(power, outputStack);
                    return true;
                }
            }
        }
        return false;
    }

//    static List<Box> expandSweeping(List<Box> boxes, World world, Hopper hopper) {
//        if(hopper instanceof HopperWithEnchantment hopperWithEnchantment && hopperWithEnchantment.getEnchantmentDictionary().hasAnyEnchantment(RANGE)) {
//            int expand = hopperWithEnchantment.getEnchantmentDictionary().getValue(RANGE);
//            return boxes.stream().map(it -> it.expand(1.0 * expand, 0.0, 1.0 * expand)).collect(Collectors.toList());
//        }
//        return boxes;
//    }

//    static void offset(World world, Hopper hopper, CallbackInfoReturnable<List<ItemEntity>> info) {
//        if(hopper instanceof HopperWithEnchantment hopperWithEnchantment && hopperWithEnchantment.getEnchantmentDictionary().hasAnyEnchantment(OFFSET)) {
//            info.setReturnValue(getInputZones(hopper).getBoundingBoxes().stream().flatMap(box -> world.getEntitiesByClass(ItemEntity.class, offsetCollectZone(box, hopperWithEnchantment), EntityPredicates.VALID_ENTITY).stream()).collect(Collectors.toList()));
//        }
//    }

//    static Box offsetCollectZone(Box box, HopperWithEnchantment hopperWithEnchantment)  {
//        int offset = hopperWithEnchantment.getEnchantmentDictionary().getValue(OFFSET);
//        return new Box(box.minX, Math.ceil(box.minY), box.minZ, box.maxX, box.maxY, box.maxZ).offset(hopperWithEnchantment.getHopperX() - 0.5, hopperWithEnchantment.getHopperY() - 0.5 + (offset * 1.0), hopperWithEnchantment.getHopperZ() - 0.5);
//    }

//    static VoxelShape getInputZones(Hopper hopper) {
//
//        if(hopper instanceof HopperWithEnchantment hopperWithEnchantment && hopperWithEnchantment.getEnchantmentDictionary().hasAnyEnchantment(OFFSET)) {
//            return Hopper.ABOVE_SHAPE;
//        }
//        return hopper.getInputAreaShape();
//    }

//    static double hopperOffset(Object hopper) {
//        AtomicInteger value = new AtomicInteger(0);
//        BlockWithEnchantment.compute(hopper, HopperWithEnchantment.class, OFFSET, it -> value.set(it.getEnchantmentDictionary().computeValue(lvl -> lvl * 1.0, OFFSET)));
//        return 0.0;
//    }

    double getHopperX();
    double getHopperY();
    double getHopperZ();
    boolean vw$getShouldOffset();
    void vw$setShouldOffset(boolean value);

    DefaultedList<ItemStack> getInventory();

    boolean vw$isHopperEmpty();
    IntStream vw$getOutputAvailableSlots(Inventory inventory, Direction facing);
    void vw$setHopperCooldown(int cooldown);
    void vw$markHopperDirty(World world, BlockPos pos, BlockState state);
    boolean vw$isHopperFull();
    boolean vw$isInvFull(Inventory inventory, Direction direction);
    DefaultedList<ItemStack> vw$getInvList();
    Inventory vw$getOutInventory(World world, BlockPos pos, BlockState state);

}
