package net.orandja.strawberry.intf;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.chocoflavor.utils.NBTUtils;
import net.orandja.chocoflavor.utils.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface StrawberryItemHandler extends StrawberryObject {

    Map<StrawberryItemHandler, List<Consumer<ItemStack>>> transformers = new HashMap<>();

    ItemStack transform(ItemStack sourceStack);

    default ItemStack transform(ItemStack sourceStack, Item replacementItem, int customModelData) {
        return GlobalUtils.apply(_transform(sourceStack, replacementItem, customModelData), this::applyCustomTransformers);
    }

    static ItemStack _transform(ItemStack sourceStack, Item replacementItem, int customModelData) {
        return GlobalUtils.create(() -> new ItemStack(replacementItem, sourceStack.getCount()), stack -> {
            if(sourceStack.hasNbt())
                stack.setNbt(sourceStack.getOrCreateNbt().copy());
            stack.getOrCreateNbt().putInt("CustomModelData", customModelData);
            if (!sourceStack.hasNbt() || !sourceStack.getNbt().contains("display")) {
                NBTUtils.getTagOrCompute(stack.getOrCreateNbt(), "display", display -> display.putString("Name", TextUtils.getNonItalicTranslatable("item.minecraft." + Registries.ITEM.getId(sourceStack.getItem()).getPath())));
            }
        });
    }

    default void addCustomTransformer(Consumer<ItemStack> customTransformer) {
        transformers.computeIfAbsent(this, key -> new ArrayList<>()).add(customTransformer);
    };

    default void applyCustomTransformers(ItemStack stack) {
        if(transformers.containsKey(this))
            transformers.get(this).forEach(transformer -> transformer.accept(stack));
    }

    static ItemStack createStrawberryStack(Item item, int customModelData, String name) {
        return createStrawberryStack(item, customModelData, name, name);
    }

    static ItemStack createStrawberryStack(Item item, int customModelData, String name, String texture) {
        return GlobalUtils.create(() -> new ItemStack(item), stack -> {
            stack.getOrCreateNbt().putInt("CustomModelData", customModelData);
            NBTUtils.getTagOrCompute(stack.getOrCreateNbt(), "display", display -> display.putString("Name", "{\"text\":\"" + name + "\",\"italic\":\"false\"}"));
        });
    }
}
