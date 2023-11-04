package net.orandja.strawberry.mods.core.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.orandja.strawberry.mods.core.CustomItemsAndBlocks;
import net.orandja.strawberry.mods.core.intf.StrawberryBlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockUpdateS2CPacket.class)
public class BlockUpdateS2CPacketMixin {

    @Shadow @Final private BlockState state;

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeRegistryValue(Lnet/minecraft/util/collection/IndexedIterable;Ljava/lang/Object;)V", shift = At.Shift.BEFORE), cancellable = true)
    public void writeCustomBlock(PacketByteBuf buf, CallbackInfo info) {
        CustomItemsAndBlocks.interceptBlockUpdates(this.state, buf, info);
    }

}
