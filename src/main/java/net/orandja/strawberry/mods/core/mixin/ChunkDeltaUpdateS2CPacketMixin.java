package net.orandja.strawberry.mods.core.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.orandja.chocoflavor.ChocoFlavor;
import net.orandja.strawberry.mods.core.intf.BlockStateTransformer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkDeltaUpdateS2CPacket.class)
public class ChunkDeltaUpdateS2CPacketMixin {

    @Redirect(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getRawIdFromState(Lnet/minecraft/block/BlockState;)I"))
    public int writeCustomBlockState(BlockState state) {
        if(state.getBlock() instanceof BlockStateTransformer blockStateTransformer) {
            return Block.getRawIdFromState(blockStateTransformer.transform(state));
        }
        return Block.getRawIdFromState(state);
    }

}
