package net.orandja.strawberry.mods.core;

import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.orandja.chocoflavor.ChocoFlavor;
import net.orandja.chocoflavor.utils.Utils;
import net.orandja.strawberry.mods.core.intf.StrawberryBlockState;
import net.orandja.strawberry.mods.resourcepack.StrawberryResourcePackGenerator;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public abstract class CustomItemsAndBlocks {

    public static void beforeLaunch() {
        StrawberryResourcePackGenerator.tripWireModels.put(TripWireBlockData.fromID(1), "minecraft:block/wheat_stage0");
    }

    public static BlockState fullTripWireState = Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.POWERED, true).with(TripwireBlock.ATTACHED, true).with(TripwireBlock.DISARMED, true).with(TripwireBlock.NORTH, true).with(TripwireBlock.SOUTH, true).with(TripwireBlock.EAST, true).with(TripwireBlock.WEST, true);
    public static BlockState wheatStage0State = TripWireBlockData.fromID(1).generateState();

    public interface BlockStateSupplier {
        int get(BlockState state);
    }

    public static void interceptStringStack(ItemStack stack) {
        if(stack.isOf(Items.STRING)) {
            stack.getOrCreateNbt().put("BlockStateTag", Utils.create(NbtCompound::new, tag -> {
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

    public static int interceptBlockState(BlockState state, BlockStateSupplier supplier, int defaultValue) {
        if(state.getBlock() instanceof StrawberryBlockState blockStateTransformer) {
            return supplier.get(blockStateTransformer.transform(state));
        }
        if(state.isOf(Blocks.NOTE_BLOCK)) {
            return supplier.get(Blocks.NOTE_BLOCK.getDefaultState());
        }
        if(state.isOf(Blocks.TRIPWIRE)) {
//            Utils.log("interceptBlockState", state, supplier.get(state), fullTripWireState, supplier.get(fullTripWireState));
            return supplier.get(fullTripWireState);
        }

        return defaultValue;
    }

    public static void interceptBlockUpdates(BlockState state, PacketByteBuf buf, CallbackInfo info) {
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
