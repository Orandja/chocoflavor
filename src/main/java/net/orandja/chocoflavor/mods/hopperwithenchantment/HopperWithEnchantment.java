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

    EnchantmentArraySetting SPEED = new EnchantmentArraySetting("hopper.speed.enchantments", new Enchantment[] { Enchantments.EFFICIENCY });

    EnchantmentArraySetting FILTER = new EnchantmentArraySetting("hopper.filter.enchantments", new Enchantment[] { Enchantments.SILK_TOUCH});

    EnchantmentArraySetting OUTPUT = new EnchantmentArraySetting("hopper.output.enchantments", new Enchantment[] { Enchantments.MENDING });

    EnchantmentArraySetting OFFSET = new EnchantmentArraySetting("hopper.offset.enchantments", new Enchantment[] { Enchantments.KNOCKBACK });

    EnchantmentArraySetting RANGE = new EnchantmentArraySetting("hopper.range.enchantments", new Enchantment[] { Enchantments.SWEEPING });
    Enchantment[] VALID_ENCHANTMENTS = BlockWithEnchantment.concat(SPEED.getValue(), FILTER.getValue(), OUTPUT.getValue(), OFFSET.getValue(), RANGE.getValue());

    static void beforeLaunch() {
        EnchantMore.addBasic(Items.HOPPER, VALID_ENCHANTMENTS);
    }


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
