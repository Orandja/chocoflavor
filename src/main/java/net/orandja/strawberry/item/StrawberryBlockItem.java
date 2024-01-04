package net.orandja.strawberry.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.strawberry.block.NoteBlockData;
import net.orandja.strawberry.intf.StrawberryItemHandler;

public class StrawberryBlockItem extends BlockItem implements StrawberryItemHandler {

    private final int customDataModel;
    private final NoteBlockData noteblockData;

    public StrawberryBlockItem(Block block, int customDataModel, Settings settings) {
        super(block, settings);
        this.customDataModel = customDataModel;
        this.noteblockData = NoteBlockData.fromID(this.customDataModel);
    }

    @Override
    public ItemStack transform(ItemStack sourceStack) {
        return GlobalUtils.apply(transform(sourceStack, Items.NOTE_BLOCK, this.customDataModel), it -> {
            it.getOrCreateNbt().put("BlockStateTag", GlobalUtils.apply(new NbtCompound(), tag -> {
                tag.putString("instrument", noteblockData.instrument().asString());
                tag.putString("powered", noteblockData.powered() ? "true": "false");
                tag.putInt("note", noteblockData.note());
            }));
        });


    }
}
