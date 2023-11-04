package net.orandja.strawberry.mods.core.item;

import net.minecraft.block.Block;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.orandja.strawberry.mods.core.intf.StrawberryItem;

public class SimpleSeedItem extends AliasedBlockItem implements StrawberryItem {

    private final String name;
    private final int customDataModel;
    private final Item replacementItem;
    private final String texture;

    public SimpleSeedItem(Block block, String name, String texture, Item replacementItem, int customDataModel, Settings settings) {
        super(block, settings);
        this.name = name;
        this.texture = texture;
        this.customDataModel = customDataModel;
        this.replacementItem = replacementItem;
    }

    public SimpleSeedItem(Block block, String name, Item replacementItem, int customDataModel, Settings settings) {
        this(block, name, null, replacementItem, customDataModel, settings);
    }

    @Override
    public ItemStack transform(ItemStack sourceStack) {
        return transform(sourceStack, this.replacementItem, this.customDataModel);
    }

    @Override
    public void register() {
        register(this.replacementItem, this.customDataModel, Registries.ITEM.getId(this).getPath(), texture == null ? Registries.ITEM.getId(this).getPath() : this.texture);
    }
}
