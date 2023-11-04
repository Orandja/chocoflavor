package net.orandja.chocoflavor.mods.deepstoragebarrel.mixin;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Direction;
import net.orandja.chocoflavor.mods.deepstoragebarrel.DeepStorageBarrel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.IntStream;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {

    @Inject(method = "getAvailableSlots", at = @At("HEAD"), cancellable = true)
    private static void getAvailableSlots(Inventory inventory, Direction side, CallbackInfoReturnable<IntStream> info) {
        if(inventory instanceof DeepStorageBarrel barrel && barrel.hasEnchantments()) {
            info.setReturnValue(IntStream.range(0, inventory.size()).map(it -> inventory.size() - 1 - it));
        }
    }

}
