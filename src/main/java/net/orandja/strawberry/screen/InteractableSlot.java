package net.orandja.strawberry.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.function.Consumer;

public class InteractableSlot extends Slot {
    private final ItemStack forcedStack;
    private final Consumer<InteractableSlot> onTake;

    public InteractableSlot(Inventory inventory, int index, int x, int y, ItemStack forcedStack, Consumer<InteractableSlot> onTake) {
        super(inventory, index, x, y);
        this.forcedStack = forcedStack;
        this.onTake = onTake;
    }

    @Override
    public ItemStack getStack() {
        return this.forcedStack;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        onTake.accept(this);
        this.markDirty();
    }

    public void setStackNoCallbacks(ItemStack stack) {
        this.markDirty();
    }

    public ItemStack takeStack(int amount) {
        onTake.accept(this);
        return ItemStack.EMPTY;
    }
}
