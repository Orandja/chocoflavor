package net.orandja.chocoflavor.mods.core.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.orandja.chocoflavor.mods.core.EnchantMore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(Enchantment.class)
abstract class EnchantmentMixin implements EnchantMore {

    @Shadow public abstract boolean isAcceptableItem(ItemStack stack);

    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        itemAcceptsEnchantment(this, stack, info);
    }
}
