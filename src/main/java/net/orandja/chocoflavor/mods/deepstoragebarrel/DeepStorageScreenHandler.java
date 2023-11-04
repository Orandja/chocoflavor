package net.orandja.chocoflavor.mods.deepstoragebarrel;

import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.orandja.chocoflavor.utils.MathUtils;

public class DeepStorageScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final PlayerInventory playerInventory;

    protected DeepStorageScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ScreenHandlerType.GENERIC_9X6, syncId);
        this.playerInventory = playerInventory;
        this.inventory = inventory;

        checkSize(inventory, 54);
        inventory.onOpen(playerInventory.player);

        MathUtils.grid(9, 5, (x, y) -> addSlot(new DeepStorageSlot(inventory, x + (y * 9), x + (y * 9), 8 + (x * 18), 18 + (y * 18))));
        MathUtils.grid(9, (x, y) -> addSlot(new DeepStorageSlot(inventory, (inventory.size() - 9) + x, x + 18, 8 + (x * 18), 18 + (2 * 18))));

        MathUtils.grid(9, 3, (x, y) -> addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18)));
        MathUtils.grid(9, (x, y) -> addSlot(new Slot(playerInventory, x, 8 + x * 18, 142)));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasStack()) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack2 = slot.getStack();
        ItemStack itemStack = itemStack2.copy();
        if (index < 54) {
            if (!insertItem(itemStack2, 54, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!insertItem(itemStack2, 0, 54, false)) {
            return ItemStack.EMPTY;
        }

        if (itemStack2.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
            return ItemStack.EMPTY;
        } else {
            slot.markDirty();
        }

        return itemStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        inventory.onClose(player);
    }
}
