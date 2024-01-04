package net.orandja.strawberry.mixin;

import net.minecraft.enchantment.*;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.strawberry.material.StrawberryToolMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {
        BindingCurseEnchantment.class,
        DamageEnchantment.class,
        EfficiencyEnchantment.class,
        ThornsEnchantment.class,
        UnbreakingEnchantment.class,

        Enchantment.class
})
public abstract class StrawberryCustomTools_EnchantmentMixin {

    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    public void checkForCustomMaterial(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        if(stack.getItem() instanceof ToolItem toolItem && toolItem.getMaterial() instanceof StrawberryToolMaterial toolMaterial) {
            GlobalUtils.applyAs(this, Enchantment.class, it -> info.setReturnValue(toolMaterial.checkForEnchantment(it, stack)));
        }
    }
}
