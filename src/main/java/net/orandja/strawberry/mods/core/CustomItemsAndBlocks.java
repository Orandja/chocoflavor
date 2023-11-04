package net.orandja.strawberry.mods.core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TripwireBlock;
import net.minecraft.network.PacketByteBuf;
import net.orandja.chocoflavor.utils.Utils;
import net.orandja.strawberry.mods.core.intf.StrawberryBlockState;
import net.orandja.strawberry.mods.resourcepack.StrawberryResourcePackGenerator;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public abstract class CustomItemsAndBlocks {

    public static void beforeLaunch() {
        StrawberryResourcePackGenerator.generate();
    }

    public static BlockState fullTripWireState = Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.ATTACHED, true).with(TripwireBlock.NORTH, true).with(TripwireBlock.SOUTH, true).with(TripwireBlock.EAST, true).with(TripwireBlock.WEST, true);

    public interface BlockStateSupplier {
        int get(BlockState state);
    }

    public static int interceptBlockState(BlockState state, BlockStateSupplier supplier, int defaultValue) {
        if(state.getBlock() instanceof StrawberryBlockState blockStateTransformer) {
            return supplier.get(blockStateTransformer.transform(state));
        }
        if(state.getBlock().equals(Blocks.NOTE_BLOCK)) {
            return supplier.get(Blocks.NOTE_BLOCK.getDefaultState());
        }
        if(state.getBlock().equals(Blocks.TRIPWIRE)) {
            Utils.log(state, fullTripWireState);
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

        if(state.getBlock().equals(Blocks.NOTE_BLOCK)) {
            buf.writeRegistryValue(Block.STATE_IDS, Blocks.NOTE_BLOCK.getDefaultState());
            info.cancel();
        }

        if(state.getBlock().equals(Blocks.TRIPWIRE)) {
            Utils.log(state, fullTripWireState);
            buf.writeRegistryValue(Block.STATE_IDS, fullTripWireState);
            info.cancel();
        }
    }
}
