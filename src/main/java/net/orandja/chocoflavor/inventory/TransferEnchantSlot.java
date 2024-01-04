package net.orandja.chocoflavor.inventory;


import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.orandja.chocoflavor.utils.StackUtils;

public class TransferEnchantSlot extends Slot {

    public TransferEnchantSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isDamageable() || stack.isOf(Items.ENCHANTED_BOOK) || StackUtils.hasAnyEnchantments(stack) || stack.isOf(Items.BOOK);
    }
}