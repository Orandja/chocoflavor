package net.orandja.chocoflavor.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ExperienceOrbEntity.class)
public abstract class ChocoEnchantments_ExperienceOrbEntityMixin {
    @Redirect(method = "repairPlayerGears", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V"))
    public void setDamage(ItemStack stack, int value) {
        int mendingLevel = EnchantmentHelper.getLevel(Enchantments.MENDING, stack);
        if(mendingLevel > 1) {
            int repairAmount = stack.getDamage() - value;
            value = Math.max(0, stack.getDamage() - (repairAmount * mendingLevel));
        }

        stack.setDamage(value);
    }
}
