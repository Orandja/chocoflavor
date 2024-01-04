package net.orandja.strawberry.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.orandja.strawberry.intf.StrawberryBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkDeltaUpdateS2CPacket.class)
public class StrawberryCustomBlocks_ChunkDeltaUpdateS2CPacketMixin {

    @Redirect(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getRawIdFromState(Lnet/minecraft/block/BlockState;)I"))
    public int writeCustomBlockState(BlockState state) {
        if(state.getBlock() instanceof StrawberryBlockState blockStateTransformer) {
            return Block.getRawIdFromState(blockStateTransformer.transform(state));
        }
        return Block.getRawIdFromState(state);
    }

}
