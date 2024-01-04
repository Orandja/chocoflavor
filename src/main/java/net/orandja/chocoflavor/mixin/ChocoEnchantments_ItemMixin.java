package net.orandja.chocoflavor.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ChocoEnchantments_ItemMixin {

//    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
//    public void isChocoEnchantable(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
//
//    }

}
