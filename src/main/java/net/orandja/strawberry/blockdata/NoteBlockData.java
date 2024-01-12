package net.orandja.strawberry.blockdata;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.orandja.chocoflavor.utils.NBTUtils;
import org.apache.commons.lang3.ArrayUtils;

import static net.minecraft.state.property.Properties.*;

public record NoteBlockData(int note, Instrument instrument, boolean powered) {

    public static final Instrument[] instruments = new Instrument[] {
            Instrument.HARP,
            Instrument.BASEDRUM,
            Instrument.SNARE,
            Instrument.HAT,
            Instrument.BASS,
            Instrument.FLUTE,
            Instrument.BELL,
            Instrument.GUITAR,
            Instrument.CHIME,
            Instrument.XYLOPHONE,
            Instrument.IRON_XYLOPHONE,
            Instrument.COW_BELL,
            Instrument.DIDGERIDOO,
            Instrument.BIT,
            Instrument.BANJO,
            Instrument.PLING,
            Instrument.ZOMBIE,
            Instrument.SKELETON,
            Instrument.CREEPER,
            Instrument.DRAGON,
            Instrument.WITHER_SKELETON,
            Instrument.PIGLIN,
            Instrument.CUSTOM_HEAD
    };

    public static NoteBlockData fromID(int id) {
        final int x = id / (2 * 22);
        return new NoteBlockData(x, instruments[(id - (x * 44)) / 2], (id % 2) == 1);
    }

    public static ItemStack getStack(int id, int count) {
        NoteBlockData data = fromID(id);

        ItemStack stack = new ItemStack(Items.NOTE_BLOCK, count);
        stack.getOrCreateNbt().putInt("CustomModelData", id);
        stack.getOrCreateNbt().put(BlockItem.BLOCK_STATE_TAG_KEY, NBTUtils.createBlankCompound(blockStateTag -> {
            blockStateTag.putInt("note", data.note);
            blockStateTag.putString("instrument", data.instrument.asString());
            blockStateTag.putBoolean("powered", data.powered);
        }));

        return stack;
    }

    public static BlockState assignStateProperties(int id) {
        return fromID(id).generateState();
    }

    public BlockState generateState() {
        return Blocks.NOTE_BLOCK.getDefaultState().with(INSTRUMENT, instrument).with(NOTE, note).with(POWERED, powered);
    }

    public int toID() {
        return (note * 44) +
                (ArrayUtils.indexOf(instruments, instrument) * 2) +
                (powered ? 1 : 0);
    }

    public String toBlockStateString() {
        return "instrument="+ instrument.asString() +",note="+ note +",powered=" + powered;
    }

}
