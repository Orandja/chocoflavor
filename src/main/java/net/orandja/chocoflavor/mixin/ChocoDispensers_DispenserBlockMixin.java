package net.orandja.chocoflavor.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.orandja.chocoflavor.ChocoDispensers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(DispenserBlock.class)
public abstract class ChocoDispensers_DispenserBlockMixin extends BlockWithEntity {
    protected ChocoDispensers_DispenserBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "dispense", at = @At("HEAD"), cancellable = true)
    void dispense(ServerWorld world, BlockState state, BlockPos pos, CallbackInfo info) {
        ChocoDispensers.dispense(world, pos, info);
    }
}
