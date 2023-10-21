package net.orandja.strawberry.mods.core.intf;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.orandja.chocoflavor.utils.NBTUtils;
import net.orandja.chocoflavor.utils.Utils;
import net.orandja.strawberry.mods.resourcepack.StrawberryResourcePackGenerator;

public interface StrawberryItem extends CustomRegistry {
    ItemStack transform(ItemStack sourceStack);

    default ItemStack transform(ItemStack sourceStack, Item replacementItem, int customModelData, String name) {
        return _transform(sourceStack, replacementItem, customModelData, name);
    }

    static ItemStack _transform(ItemStack sourceStack, Item replacementItem, int customModelData, String name) {
        return Utils.create(() -> new ItemStack(replacementItem), stack -> {
            stack.getOrCreateNbt().putInt("CustomModelData", customModelData);
            if (!sourceStack.hasNbt() || !sourceStack.getNbt().contains("display")) {
                NBTUtils.getTagOrCompute(stack.getOrCreateNbt(), "display", display -> display.putString("Name", "{\"text\":\"" + name + "\",\"italic\":\"false\"}"));
            }
        });
    }

    void register();

    default void register(Item replacementItem, int customModelData, String modelName, String texture) {
        _register(replacementItem, customModelData, modelName, texture);
    }

    static void _register(Item replacementItem, int customModelData, String modelName, String texture) {
        StrawberryResourcePackGenerator.getModelData(Registries.ITEM.getId(replacementItem).getPath()).add(new StrawberryResourcePackGenerator.CustomModelData(customModelData, modelName, texture));
    }

    static ItemStack createStrawberryStack(Item item, int customModelData, String name) {
        return createStrawberryStack(item, customModelData, name, name);
    }

    static ItemStack createStrawberryStack(Item item, int customModelData, String name, String texture) {
        _register(item, customModelData, name, texture);
        return Utils.create(() -> new ItemStack(item), stack -> {
            stack.getOrCreateNbt().putInt("CustomModelData", customModelData);
            NBTUtils.getTagOrCompute(stack.getOrCreateNbt(), "display", display -> display.putString("Name", "{\"text\":\"" + name + "\",\"italic\":\"false\"}"));
        });
    }
}
