package net.orandja.strawberry.mods.core.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.orandja.strawberry.mods.core.intf.StrawberryItem;

public class SimpleCustomItem extends Item implements StrawberryItem {

    private final String name;
    private final int customDataModel;
    private final Item replacementItem;
    private final String texture;

    public SimpleCustomItem(String name, String texture, Item replacementItem, int customDataModel, Settings settings) {
        super(settings);
        this.name = name;
        this.texture = texture;
        this.customDataModel = customDataModel;
        this.replacementItem = replacementItem;
    }

    public SimpleCustomItem(String name, Item replacementItem, int customDataModel, Settings settings) {
        this(name, name, replacementItem, customDataModel, settings);
    }

    @Override
    public ItemStack transform(ItemStack sourceStack) {
        return transform(sourceStack, this.replacementItem, this.customDataModel, this.name);
    }

    @Override
    public void register() {
        register(this.replacementItem, this.customDataModel, Registries.ITEM.getId(this).getPath(), this.texture);
    }
}
