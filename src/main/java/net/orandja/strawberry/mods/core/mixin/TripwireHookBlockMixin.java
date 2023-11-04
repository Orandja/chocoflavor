package net.orandja.strawberry.mods.core.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TripwireBlock;
import net.minecraft.block.TripwireHookBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.ChocoFlavor;
import net.orandja.chocoflavor.utils.Utils;
import net.orandja.strawberry.mods.core.CustomItemsAndBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TripwireHookBlock.class)
public class TripwireHookBlockMixin {

    @Inject(method = "onPlaced", at = @At("TAIL"))
    public void onBlockPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        Utils.log("onBlockPlaced");
    }

    @Inject(method = "update", at = @At(target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", value = "INVOKE", shift = At.Shift.AFTER))
    public void updateState(World world, BlockPos pos, BlockState state, boolean beingRemoved, boolean bl, int i, BlockState blockState, CallbackInfo ci) {
        if(!world.isClient && state.isOf(Blocks.TRIPWIRE)) {
            Utils.log("CALLED");
            BlockUpdateS2CPacket packet = new BlockUpdateS2CPacket(pos, CustomItemsAndBlocks.fullTripWireState);
            for (ServerPlayerEntity serverPlayerEntity : ChocoFlavor.serverReference.get().getPlayerManager().getPlayerList()) {
                serverPlayerEntity.networkHandler.sendPacket(packet);
            }
        }
    }
}
