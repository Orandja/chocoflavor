//package net.orandja.chocoflavor.mods.core.mixin;
//
//import net.minecraft.block.BlockState;
//import net.minecraft.block.entity.BlockEntityType;
//import net.minecraft.block.entity.Hopper;
//import net.minecraft.block.entity.HopperBlockEntity;
//import net.minecraft.block.entity.LockableContainerBlockEntity;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import net.orandja.chocoflavor.mods.core.ProtectBlock;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(HopperBlockEntity.class)
//public abstract class HopperBlockEntityMixin extends LockableContainerBlockEntity implements ProtectBlock {
//
//    protected HopperBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
//        super(blockEntityType, blockPos, blockState);
//    }
//
//    @Inject(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z", at = @At("HEAD"), cancellable = true)
//    private static void extract(World world, Hopper hopper, CallbackInfoReturnable<Boolean> info) {
//        ProtectBlock.preventsExtraction(world, hopper, info);
//    }
//}
