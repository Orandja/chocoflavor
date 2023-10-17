package net.orandja.strawberry.mods.core.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.orandja.strawberry.mods.core.intf.ItemStackTransformer;

public class SimpleBlockItem extends BlockItem implements ItemStackTransformer {

    private final String name;
    private final int customDataModel;

    public SimpleBlockItem(Block block, String name, int customDataModel, Settings settings) {
        super(block, settings);
        this.name = name;
        this.customDataModel = customDataModel;
    }

    @Override
    public ItemStack create(ItemStack sourceStack) {
        return create(sourceStack, Items.NOTE_BLOCK, this.customDataModel, name);
    }
}
