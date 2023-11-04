package net.orandja.strawberry.mods.core.intf;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.orandja.chocoflavor.utils.NBTUtils;
import net.orandja.chocoflavor.utils.TextUtils;
import net.orandja.chocoflavor.utils.Utils;
import net.orandja.strawberry.mods.resourcepack.StrawberryResourcePackGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface StrawberryItem extends CustomRegistry {

    Map<StrawberryItem, List<Consumer<ItemStack>>> transformers = new HashMap<>();

    ItemStack transform(ItemStack sourceStack);

    default ItemStack transform(ItemStack sourceStack, Item replacementItem, int customModelData) {
        return Utils.apply(_transform(sourceStack, replacementItem, customModelData), this::applyCustomTransformers);
    }

    static ItemStack _transform(ItemStack sourceStack, Item replacementItem, int customModelData) {
        return Utils.create(() -> new ItemStack(replacementItem, sourceStack.getCount()), stack -> {
            if(sourceStack.hasNbt())
                stack.setNbt(sourceStack.getOrCreateNbt().copy());
            stack.getOrCreateNbt().putInt("CustomModelData", customModelData);
            if (!sourceStack.hasNbt() || !sourceStack.getNbt().contains("display")) {
                NBTUtils.getTagOrCompute(stack.getOrCreateNbt(), "display", display -> display.putString("Name", TextUtils.getNonItalicTranslatable("item.minecraft." + Registries.ITEM.getId(sourceStack.getItem()).getPath())));
            }
        });
    }

    void register();

    default void addCustomTransformer(Consumer<ItemStack> customTransformer) {
        transformers.computeIfAbsent(this, key -> new ArrayList<>()).add(customTransformer);
    };

    default void applyCustomTransformers(ItemStack stack) {
        if(transformers.containsKey(this))
            transformers.get(this).forEach(transformer -> transformer.accept(stack));
    }


    default void register(Item replacementItem, int customModelData, String modelName, String texture) {
        _register(replacementItem, customModelData, modelName, texture);
    }

    default void register(Item replacementItem, int customModelData, String modelName) {
        _register(replacementItem, customModelData, modelName, modelName);
    }

    static void _register(Item replacementItem, int customModelData, String modelName, String texture) {
        StrawberryResourcePackGenerator.getModelData(Registries.ITEM.getId(replacementItem).getPath()).add(new StrawberryResourcePackGenerator.CustomModelData(customModelData, modelName, new String[]{ texture }));
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
