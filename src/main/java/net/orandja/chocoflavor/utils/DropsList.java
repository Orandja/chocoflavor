package net.orandja.chocoflavor.utils;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DropsList {

    private final List<ItemStack> drops = new ArrayList<>();

    public DropsList addDrop(ItemStack stack) {
        for (ItemStack drop : this.drops) {
            if(StackUtils.canMerge(drop, stack)) {
                drop.increment(stack.getCount());
                return this;
            }
        }
        drops.add(stack.copy());
        return this;
    }

    public DropsList getDrops(Consumer<ItemStack> consumer) {
        this.drops.forEach(consumer);
        return this;
    }

    public DropsList addDrop(List<ItemStack> droppedStacks) {
        droppedStacks.forEach(this::addDrop);
        return this;
    }
}
