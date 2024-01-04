package net.orandja.chocoflavor.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class DispenserCraftingInventory extends CraftingInventory {

    private final int width;
    private final int height;
    private final DefaultedList<ItemStack> stacks;

    public DispenserCraftingInventory(int width, int height, List<ItemStack> stacks) {
        super(null, width, height);
        this.width = width;
        this.height = height;
        this.stacks = DefaultedList.ofSize(width * height, ItemStack.EMPTY);
        for (int i = 0; i < this.stacks.size(); i++) {
            this.stacks.set(i, stacks.get(i));
        }
    }

    @Override
    public int size() {
        return this.stacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.stacks) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot >= this.size() ? ItemStack.EMPTY : this.stacks.get(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.stacks.set(slot, stack);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.stacks, slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.stacks, slot, amount);
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.stacks.clear();
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.height;
    }

    @Override
    public void provideRecipeInputs(RecipeMatcher finder) {
        this.stacks.forEach(finder::addInput);
    }
}