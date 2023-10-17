package net.orandja.strawberry.mods.core.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.orandja.strawberry.mods.core.intf.ItemStackTransformer;

public class SimpleCustomItem extends Item implements ItemStackTransformer {

    private final String name;
    private final int customDataModel;
    private final Item replacementItem;

    public SimpleCustomItem(String name, Item replacementItem, int customDataModel, Settings settings) {
        super(settings);
        this.name = name;
        this.customDataModel = customDataModel;
        this.replacementItem = replacementItem;
    }

    @Override
    public ItemStack create(ItemStack sourceStack) {
        return create(sourceStack, this.replacementItem, this.customDataModel, this.name);
    }
}
