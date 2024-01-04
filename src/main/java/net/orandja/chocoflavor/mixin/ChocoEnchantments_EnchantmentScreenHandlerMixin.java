package net.orandja.chocoflavor.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.orandja.chocoflavor.ChocoEnchantments;
import net.orandja.chocoflavor.enchantment.ItemEnchantmentsRegistry;
import net.orandja.chocoflavor.utils.GlobalUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentScreenHandler.class)
public abstract class ChocoEnchantments_EnchantmentScreenHandlerMixin {

    @Redirect(method = "onContentChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEnchantable()Z"))
    public boolean isChocoEnchantable(ItemStack itemStack) {
        return GlobalUtils.runOrSupply(ChocoEnchantments.getRegistry(itemStack.getItem()), itemStack::isEnchantable, ItemEnchantmentsRegistry::isTableEnchantable);
    }

}
