package net.orandja.strawberry.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.strawberry.intf.StrawberryLightningRodInteractable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LightningEntity.class)
public abstract class StrawberryCustomBlocks_LightningEntityMixin extends Entity {

    public StrawberryCustomBlocks_LightningEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "powerLightningRod", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "INVOKE", target = "Lnet/minecraft/block/LightningRodBlock;setPowered(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", shift = At.Shift.AFTER))
    public void onPowerLightningRod(CallbackInfo info, BlockPos pos) {
        if(!this.getWorld().isClient && !this.getWorld().isOutOfHeightLimit(pos.getY()) && this.getWorld().getBlockState(pos.down()).getBlock() instanceof StrawberryLightningRodInteractable interactable) {
            GlobalUtils.applyAs(this, LightningEntity.class, it -> interactable.onLightningInteract(it, pos.down()));
        }
    }
}
