package net.orandja.chocoflavor.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public abstract class SlotUtils {

    public static void markDirtyIfEmpty(Slot slot, ItemStack stack) {
        if(stack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }
    }

}
