//package net.orandja.chocoflavor.mods.core.mixin;
//
//import net.minecraft.enchantment.Enchantment;
//import net.minecraft.enchantment.EnchantmentTarget;
//import net.minecraft.enchantment.InfinityEnchantment;
//import net.minecraft.enchantment.MendingEnchantment;
//import net.minecraft.entity.EquipmentSlot;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(InfinityEnchantment.class)
//public abstract class InfinityEnchantmentMixin extends Enchantment {
//    protected InfinityEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
//        super(weight, type, slotTypes);
//    }
//
//    @Inject(method = "canAccept", at = @At("HEAD"), cancellable = true)
//    public void canAccept(Enchantment other, CallbackInfoReturnable<Boolean> info) {
//        if(other instanceof MendingEnchantment) {
//            info.setReturnValue(true);
//        }
//    }
//}