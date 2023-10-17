package net.orandja.strawberry.mods.core.intf;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.orandja.chocoflavor.utils.NBTUtils;

public interface ItemStackTransformer extends CustomRegistry {
    ItemStack create(ItemStack sourceStack);

    default ItemStack create(ItemStack sourceStack, Item replacementItem, int customModelData, String name) {
        ItemStack stack = new ItemStack(replacementItem, sourceStack.getCount());
        stack.getOrCreateNbt().putInt("CustomModelData", customModelData);
        if (!sourceStack.hasNbt() || !sourceStack.getNbt().contains("display")) {
            NBTUtils.getTagOrCompute(stack.getOrCreateNbt(), "display", display -> display.putString("Name", "{\"text\":\"" + name + "\",\"italic\":\"false\"}"));
        }
        return stack;
    }

    static ItemStack createCustomNamedStack(ItemStack stack, int customModelData, String name) {
        stack.getOrCreateNbt().putInt("CustomModelData", customModelData);
        NBTUtils.getTagOrCompute(stack.getOrCreateNbt(), "display", display -> display.putString("Name", "{\"text\":\"" + name + "\",\"italic\":\"false\"}"));
        return stack;
    }
}
