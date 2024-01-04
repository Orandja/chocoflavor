//package net.orandja.chocoflavor.mods.core.mixin;
//
//import net.minecraft.entity.EntityType;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.GameMode;
//import net.minecraft.world.World;
//import net.orandja.chocoflavor.mods.core.ProtectBlock;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@SuppressWarnings("unused")
//@Mixin(PlayerEntity.class)
//abstract class PlayerEntityMixin extends LivingEntity implements ProtectBlock {
//
//    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
//        super(entityType, world);
//    }
//
//    @Inject(at = @At("HEAD"), method = "isBlockBreakingRestricted", cancellable = true)
//    void isBlockBreakingRestricted(World world, BlockPos pos, GameMode gameMode, CallbackInfoReturnable<Boolean> info) {
//        onPlayerDestroy(world, pos, this, info);
//    }
//
//}
