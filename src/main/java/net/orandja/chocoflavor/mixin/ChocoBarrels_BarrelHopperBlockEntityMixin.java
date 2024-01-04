package net.orandja.chocoflavor.mixin;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Direction;
import net.orandja.chocoflavor.ChocoBarrels;
import net.orandja.chocoflavor.utils.GlobalUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.IntStream;

@Mixin(HopperBlockEntity.class)
public class ChocoBarrels_BarrelHopperBlockEntityMixin {

    @Inject(method = "getAvailableSlots", at = @At("HEAD"), cancellable = true)
    private static void getAvailableSlots(Inventory inventory, Direction side, CallbackInfoReturnable<IntStream> info) {
        GlobalUtils.applyAs(inventory, ChocoBarrels.Handler.class, it -> it.getReverseRange(info));
    }

}
