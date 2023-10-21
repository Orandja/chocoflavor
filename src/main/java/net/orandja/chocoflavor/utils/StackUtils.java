package net.orandja.chocoflavor.utils;

import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.orandja.chocoflavor.mods.doubletools.DoubleTools;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class StackUtils {

    public static boolean canMerge(ItemStack into, ItemStack from) {
        return canMerge(into, from, from.getCount());
    }
    public static boolean canMerge(ItemStack into, ItemStack from, int countFromSecond) {
        return Objects.equals(into.getItem(), from.getItem()) && Objects.equals(into.getDamage(), from.getDamage()) && ((into.getCount() + countFromSecond) <= into.getMaxCount()) && Objects.equals(into.getNbt(), from.getNbt());
    }

    public static boolean isGonnaBreak(ItemStack stack) {
        return stack.isEmpty() || stack.getDamage() >= (stack.getMaxDamage() - 2);
    }

    public static boolean anyGonnaBreak(ItemStack... stacks) {
        for(ItemStack stack: stacks) {
            if(isGonnaBreak(stack)) {
                return true;
            }
        }

        return false;
    }

    public static boolean anyGonnaBreak(Pair<ItemStack, ItemStack> stacksInHands) {
        return anyGonnaBreak(stacksInHands.getLeft(), stacksInHands.getRight());
    }

    public static boolean isCompatible(ItemStack stack, ItemStack other) {
        return isCompatible(stack, other, false, true);
    }

    public static boolean isCompatible(ItemStack stack, ItemStack other, boolean checkSize, boolean allowEmpty) {
        return ((stack.isEmpty() || other.isEmpty()) && allowEmpty) ||
                (isSimilar(stack, other) && (!checkSize || stack.getCount() < stack.getMaxCount()));
    }

    public static boolean hasAnyEnchantments(ItemStack stack) {
        if (stack.getNbt() != null && stack.getNbt().contains("StoredEnchantments", NbtElement.LIST_TYPE)) {
            NbtList list = stack.getNbt().getList("StoredEnchantments", NbtElement.COMPOUND_TYPE);
            return !list.isEmpty() && list.size() > 0;
        }

        return stack.hasEnchantments();
    }

    public static boolean isSimilar(ItemStack stack, ItemStack other) {
        return Objects.equals(stack.getItem(), other.getItem()) && Objects.equals(stack.getNbt(), other.getNbt());
//        return stack.isItemEqual(other) && ItemStack.areNbtEqual(stack, other);
    }

    public static int wholeCount(DefaultedList<ItemStack> list) {
        int count = 0;
        for(int index = 0; index < list.size(); index++) {
            ItemStack stack = list.get(index);
            if(!stack.isEmpty()) {
                count += stack.getCount();
            }
        }

        return count;
    }

    public static void toNBT(DefaultedList<ItemStack> list, NbtCompound nbt) {
        toNBT(list, nbt, false);
    }

    public static void toNBT(DefaultedList<ItemStack> list, NbtCompound nbt, boolean setIfEmpty) {
        NbtList nbtList = new NbtList();
        for(int index = 0; index < list.size(); index++) {
            ItemStack stack = list.get(index);
            if(!stack.isEmpty()) {
                NbtCompound tag = new NbtCompound();
                tag.putShort("Slot", (short) nbtList.size());
                stack.writeNbt(tag);
                nbtList.add(tag);
            }
        }

        if(!nbtList.isEmpty() || setIfEmpty) {
            nbt.put("Items", nbtList);
        }
    }

    public static DefaultedList<ItemStack> fromNBT(DefaultedList<ItemStack> list, NbtCompound nbt) {
        nbt.getList("Items", 10).forEach(it -> {
            if(it instanceof NbtCompound item) {
                int slot = item.getShort("Slot");
                if(slot >= 0 && slot < list.size()) {
                    list.set(slot, ItemStack.fromNbt(item));
                }
            }
        });

        return list;
    }

    public static void computeTag(ItemStack stack, Consumer<NbtCompound> consumer) {
        consumer.accept(stack.getOrCreateNbt());
    }
    public static boolean isOfItem(ItemStack stack, Item item) {
        return !stack.isEmpty() && Objects.equals(stack.getItem(), item);
    }
    public static boolean isOfItem(ItemStack stack1, ItemStack stack2) {
        return isOfItem(stack1, stack2.getItem());
    }

    public static boolean ofStack(Item item, ItemStack stack) {
        return !stack.isEmpty() && Objects.equals(item, stack.getItem());
    }

    public static void computeLore(ItemStack stack, Consumer<NbtList> consumer) {
        NBTUtils.getTagOrCompute(stack.getOrCreateNbt(), "display", display -> {
           NBTUtils.getStringListOrCompute(display, "Lore", consumer::accept);
        });
    }

    public static ToolMaterial getToolMaterial(ItemStack stack) {
        if(stack.getItem() instanceof ToolItem tool) {
            return tool.getMaterial();
        }

        if(stack.getItem() instanceof DoubleTools.Applicable tool) {
            return tool.getMaterial();
        }

        return null;
    }

    public static int convertDurability(ItemStack sourceStack, int convertedMaxDurability) {
        return (int) Math.ceil((double) (sourceStack.getDamage() * convertedMaxDurability) / sourceStack.getMaxDamage());
    }

    public static int convertDurability(ItemStack sourceStack, ToolItem convertedItem) {
        return convertDurability(sourceStack, convertedItem.getMaterial().getDurability());
    }

    public static int convertDurability(ItemStack sourceStack, ArmorItem convertedItem) {
        return convertDurability(sourceStack, convertedItem.getMaterial().getDurability(convertedItem.getType()));
    }
}
