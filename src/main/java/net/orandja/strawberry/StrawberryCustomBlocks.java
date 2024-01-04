package net.orandja.strawberry;

import net.minecraft.block.*;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.strawberry.block.StrawberryBlock;
import net.orandja.strawberry.intf.StrawberryBlockState;
import net.orandja.strawberry.item.StrawberryBlockItem;
import net.orandja.strawberry.block.MufflerBlock;
import net.orandja.strawberry.block.TripWireBlockData;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

public class StrawberryCustomBlocks {

    private static final BlockState fullTripWireState = Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.POWERED, true).with(TripwireBlock.ATTACHED, true).with(TripwireBlock.DISARMED, true).with(TripwireBlock.NORTH, true).with(TripwireBlock.SOUTH, true).with(TripwireBlock.EAST, true).with(TripwireBlock.WEST, true);
    private static final BlockState wheatStage0State = TripWireBlockData.fromID(1).generateState();

    public static Block REINFORCED_OBSIDIAN;
    public static Item REINFORCED_OBSIDIAN_ITEM;

    public static Block MUFFLER_BLOCK;
    public static Item MUFFLER_ITEM;

    public static void init() {
        REINFORCED_OBSIDIAN = Blocks.register("reinforced_obsidian", new StrawberryBlock(24, "reinforced_obsidian", it -> {
            it.mapColor(MapColor.BLACK).instrument(Instrument.BASEDRUM).requiresTool().strength(150.0f, 1200.0f);
        }));
        REINFORCED_OBSIDIAN_ITEM = Items.register(new StrawberryBlockItem(REINFORCED_OBSIDIAN, 24, new Item.Settings()));

        MUFFLER_BLOCK = Blocks.register("muffler", new MufflerBlock(25, "muffler"));
        MUFFLER_ITEM = Items.register(new StrawberryBlockItem(MUFFLER_BLOCK, 25, new Item.Settings()));
    }

    public interface BlockUpdateHandler {
        default void onBlockUpdate(BlockState state, PacketByteBuf buf, CallbackInfo info) {
            if(state.getBlock() instanceof StrawberryBlockState blockStateTransformer) {
                buf.writeRegistryValue(Block.STATE_IDS, blockStateTransformer.transform(state));
                info.cancel();
                return;
            }

            if(state.isOf(Blocks.NOTE_BLOCK)) {
                buf.writeRegistryValue(Block.STATE_IDS, Blocks.NOTE_BLOCK.getDefaultState());
                info.cancel();
            }

            if(state.isOf(Blocks.TRIPWIRE)) {
                buf.writeRegistryValue(Block.STATE_IDS, fullTripWireState);
                info.cancel();
            }

            if(state.isOf(Blocks.WHEAT) && state.get(CropBlock.AGE) == 0) {
                buf.writeRegistryValue(Block.STATE_IDS, wheatStage0State);
                info.cancel();
            }
        }
    }

    public interface BlockStateHandler {
        default int onBlockState(BlockState state, Function<BlockState, Integer> supplier, int defaultValue) {
            if(state.getBlock() instanceof StrawberryBlockState blockStateTransformer) {
                return supplier.apply(blockStateTransformer.transform(state));
            }
            if(state.isOf(Blocks.NOTE_BLOCK)) {
                return supplier.apply(Blocks.NOTE_BLOCK.getDefaultState());
            }
            if(state.isOf(Blocks.TRIPWIRE)) {
                return supplier.apply(fullTripWireState);
            }

            return defaultValue;
        }
    }

    public interface ItemStackHandler {
        default void onItemStack(ItemStack stack) {
            if(stack.isOf(Items.STRING)) {
                stack.getOrCreateNbt().put("BlockStateTag", GlobalUtils.create(NbtCompound::new, tag -> {
                    tag.putString("east", "true");
                    tag.putString("north", "true");
                    tag.putString("south", "true");
                    tag.putString("west", "true");
                    tag.putString("disarmed", "true");
                    tag.putString("attached", "true");
                    tag.putString("powered", "true");
                }));
            }
        }
    }
}
