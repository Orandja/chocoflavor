package net.orandja.chocoflavor.mods.whitelistedchestblock.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.mods.whitelistedchestblock.WhitelistedChestBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin implements WhitelistedChestBlock {

    @Inject(at = @At("HEAD"), method = "onUse", cancellable = true)
    void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> info) {
        this.onBlockUse(world, pos, player, info);
    }

    @Inject(at = @At("RETURN"), method = "onPlaced")
    void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, CallbackInfo info) {
        this.onBlockPlaced(world, pos, stack);
    }

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    void handleDoubleChest(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> info) {
        this.onPlacementState(ctx, info);
    }
}
