package net.orandja.strawberry.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class UninteractableInventory implements Inventory {

    private final int size;
    private final Inventory realInventory;
    private final Map<Integer, Integer> redirectMap = new HashMap<>();

    public UninteractableInventory(int size, Inventory realInventory, RedirectSlot... slots) {
        this.size = size;
        this.realInventory = realInventory;
        for (RedirectSlot slot : slots) {
            this.redirectMap.put(slot.fakeIndex, slot.realIndex);
        }
    }

    public ItemStack getRealStack(int fakeSlot) {
        if (!hasRealSlot(fakeSlot)) {
            return ItemStack.EMPTY;
        }

        return this.realInventory.getStack(this.redirectMap.get(fakeSlot));
    }

    public boolean hasRealSlot(int fakeSlot) {
        return this.redirectMap.containsKey(fakeSlot);
    }

    public int getRealSlot(int fakeSlot) {
        return this.redirectMap.get(fakeSlot);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.realInventory.isEmpty();
    }

    @Override
    public ItemStack getStack(int fakeSlot) {
        return getRealStack(fakeSlot);
//            return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int fakeSlot, int amount) {
        return hasRealSlot(fakeSlot)
                ? this.removeStack(this.getRealSlot(fakeSlot), amount)
                : ItemStack.EMPTY;
//            return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int fakeSlot) {
        return hasRealSlot(fakeSlot)
                ? this.removeStack(this.getRealSlot(fakeSlot))
                : ItemStack.EMPTY;
//            return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int fakeSlot, ItemStack stack) {
        if (this.hasRealSlot(fakeSlot)) {
            this.realInventory.setStack(this.getRealSlot(fakeSlot), stack);
        }
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.realInventory.canPlayerUse(player);
    }

    @Override
    public void clear() {

    }
}
