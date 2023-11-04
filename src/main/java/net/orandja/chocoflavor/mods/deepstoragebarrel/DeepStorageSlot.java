package net.orandja.chocoflavor.mods.deepstoragebarrel;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class DeepStorageSlot extends Slot {
    private final int realIndex;

    public DeepStorageSlot(Inventory inventory, int realIndex, int index, int x, int y) {
        super(inventory, index, x, y);
        this.realIndex = realIndex;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return this.inventory.getStack(0).isEmpty() || this.inventory.getStack(0).getItem() == stack.getItem();
    }

    @Override
    public ItemStack getStack() {
        return this.inventory.getStack(realIndex);
    }

    @Override
    public void setStackNoCallbacks(ItemStack stack) {
        this.inventory.setStack(this.realIndex, stack);
        this.markDirty();
    }

    @Override
    public ItemStack takeStack(int amount) {
        return inventory.removeStack(this.realIndex, amount);
//            return Utils.apply(inventory.removeStack(this.realIndex, amount), it -> this.inventory.setStack(this.realIndex, this.inventory.getStack(this.realIndex)));
    }
}
