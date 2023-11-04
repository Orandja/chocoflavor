package net.orandja.strawberry.mods.core.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.orandja.chocoflavor.utils.Utils;
import net.orandja.strawberry.mods.core.NoteBlockData;
import net.orandja.strawberry.mods.core.block.StrawberryBlock;
import net.orandja.strawberry.mods.core.intf.StrawberryItem;

public class SimpleBlockItem extends BlockItem implements StrawberryItem {

    private final int customDataModel;
    private final NoteBlockData noteblockData;

    public SimpleBlockItem(Block block, int customDataModel, Settings settings) {
        super(block, settings);
        this.customDataModel = customDataModel;
        this.noteblockData = NoteBlockData.fromID(this.customDataModel);
    }

    @Override
    public ItemStack transform(ItemStack sourceStack) {
        return Utils.apply(transform(sourceStack, Items.NOTE_BLOCK, this.customDataModel), it -> {
            it.getOrCreateNbt().put("BlockStateTag", Utils.apply(new NbtCompound(), tag -> {
                tag.putString("instrument", noteblockData.instrument().asString());
                tag.putString("powered", noteblockData.powered() ? "true": "false");
                tag.putInt("note", noteblockData.note());
            }));
        });


    }

    @Override
    public void register() {
        /* Ignore item registration since we use NoteBlocks */
    }
}
