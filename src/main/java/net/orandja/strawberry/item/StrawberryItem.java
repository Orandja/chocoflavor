package net.orandja.strawberry.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.orandja.strawberry.intf.StrawberryItemHandler;

public class StrawberryItem extends Item implements StrawberryItemHandler {

    private final int customDataModel;
    private final Item replacementItem;

    public StrawberryItem(Item replacementItem, int customDataModel, Settings settings) {
        super(settings);
        this.customDataModel = customDataModel;
        this.replacementItem = replacementItem;
    }

    @Override
    public ItemStack transform(ItemStack sourceStack) {
        return transform(sourceStack, this.replacementItem, this.customDataModel);
    }
}
