package net.orandja.chocoflavor.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.orandja.chocoflavor.accessor.CraftingInventoryAccessor;
import net.orandja.chocoflavor.accessor.CraftingScreenHandlerAccessor;
import net.orandja.chocoflavor.accessor.PlayerScreenHandlerAccessor;
import net.orandja.chocoflavor.accessor.ScreenHandlerAccessor;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public abstract class InventoryUtils {

    public static PlayerEntity getCraftingPlayer(RecipeInputInventory inventory) {
        ScreenHandler handler = getScreenHandler(inventory);
        if(handler instanceof CraftingScreenHandler && handler instanceof CraftingScreenHandlerAccessor accessor) {
            return accessor.getPlayer();
        }

        if(handler instanceof PlayerScreenHandler && handler instanceof PlayerScreenHandlerAccessor accessor) {
            return accessor.getOwner();
        }
        return null;
    }

    public static boolean hasListeners(RecipeInputInventory inventory) {
        if(getScreenHandler(inventory) instanceof ScreenHandlerAccessor accessor) {
            return !accessor.getListeners().isEmpty();
        }
        return false;
    }

    public static ScreenHandler getScreenHandler(RecipeInputInventory inventory) {
        if(inventory instanceof CraftingInventoryAccessor craftingInventory) {
            return craftingInventory.getHandler();
        }
        return null;
    }

    public static ItemStack getGridStack(CraftingInventory inventory, int x, int y) {
        return inventory.getStack(getGridIndex(x, y, inventory.getWidth()));
    }

    public static int getGridIndex(int x, int y, int width) {
        return (y * width) + x;
    }

    public interface StackProvider {
        ItemStack get(ItemStack fromStack);
    }

    public static final StackProvider noChange = fromStack -> fromStack;

    public static boolean mergeInto(ItemStack stack, Inventory output) {
        return mergeInto(stack, output, noChange);
    }
    public static boolean mergeInto(ItemStack stack, Inventory output, StackProvider provider) {
        for(int index = 0; index < output.size(); index++) {
            if(output.isValid(index, stack)) {
                ItemStack outputStack = output.getStack(index);
                if(outputStack.isEmpty()) {
                    output.setStack(index, provider.get(stack));
                    output.markDirty();
                    return true;
                } else if (StackUtils.canMerge(outputStack, stack)) {
                    outputStack.increment(provider.get(stack).getCount());
                    output.markDirty();
                    return true;
                }
            }
        }
        return false;
    }

    public static ItemStack[] toArray(Inventory inventory) {
        return toArray(inventory, true);
    }

    public static Stream<ItemStack> toStream(Inventory inventory) {
        return Arrays.stream(InventoryUtils.toArray(inventory));
    }

    public static Stream<ItemStack> toStream(Inventory inventory, boolean nonEmpty) {
        return Arrays.stream(InventoryUtils.toArray(inventory, nonEmpty));
    }
    public static ItemStack[] toArray(Inventory inventory, boolean nonEmpty) {
        ArrayList<ItemStack> list = Lists.newArrayList();
        for(int index = 0; index < inventory.size(); index++) {
            ItemStack stack = inventory.getStack(index);
            if(!nonEmpty || !stack.isEmpty()) {
                list.add(stack);
            }
        }

        return list.toArray(new ItemStack[list.size()]);
    }
}
