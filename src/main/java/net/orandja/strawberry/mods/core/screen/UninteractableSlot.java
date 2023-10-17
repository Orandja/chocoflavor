package net.orandja.strawberry.mods.core.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class UninteractableSlot extends Slot {
    public UninteractableSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }


    protected void onTake(int slot) {
    }
}
