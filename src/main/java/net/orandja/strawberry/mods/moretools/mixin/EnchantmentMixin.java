package net.orandja.strawberry.mods.moretools.mixin;

import net.minecraft.enchantment.*;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.orandja.strawberry.mods.moretools.CustomToolMaterial;
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
public abstract class EnchantmentMixin {

    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    public void checkForCustomMaterial(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        if(stack.getItem() instanceof ToolItem toolItem && toolItem.getMaterial() instanceof CustomToolMaterial toolMaterial) {
            info.setReturnValue(toolMaterial.checkForEnchantment(Enchantment.class.cast(this), stack));
        }
    }
}
