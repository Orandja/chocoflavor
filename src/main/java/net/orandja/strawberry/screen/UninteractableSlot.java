package net.orandja.strawberry.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class UninteractableSlot extends Slot {
    private final ItemStack forcedStack;

    public UninteractableSlot(Inventory inventory, int index, int x, int y, ItemStack forcedStack) {
        super(inventory, index, x, y);
        this.forcedStack = forcedStack;
    }

    public UninteractableSlot(Inventory inventory, int index, int x, int y) {
        this(inventory, index, x, y, ItemStack.EMPTY);
    }

    @Override
    public ItemStack getStack() {
        return this.forcedStack != null ? this.forcedStack : super.getStack();
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    protected void onTake(int slot) {
    }
}
