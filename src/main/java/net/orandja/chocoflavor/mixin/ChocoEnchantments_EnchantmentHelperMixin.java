package net.orandja.chocoflavor.mixin;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.orandja.chocoflavor.ChocoEnchantments;
import net.orandja.chocoflavor.enchantment.ItemEnchantmentsRegistry;
import net.orandja.chocoflavor.utils.GlobalUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(EnchantmentHelper.class)
public class ChocoEnchantments_EnchantmentHelperMixin {

    @Inject(method = "getPossibleEntries",
            at = @At("HEAD"),
        cancellable = true)
    private static void isTableEnchantable(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> info) {
        GlobalUtils.apply(ChocoEnchantments.getRegistry(stack.getItem()), registry -> {
            ArrayList<EnchantmentLevelEntry> list = Lists.newArrayList();
            for (Enchantment enchantment : Registries.ENCHANTMENT) {
                if (enchantment.isTreasure() && !treasureAllowed || !enchantment.isAvailableForRandomSelection() || !registry.isAllowedInTable(stack, enchantment)) continue;
                for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                    if (power < enchantment.getMinPower(i) || power > enchantment.getMaxPower(i)) continue;
                    list.add(new EnchantmentLevelEntry(enchantment, i));
                }
            }
            info.setReturnValue(list);
        });
    }

    @Redirect(method = "calculateRequiredExperienceLevel",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getEnchantability()I"))
    private static int calculateCustomEnchantability(Item item) {
        return GlobalUtils.runOrSupply(ChocoEnchantments.getRegistry(item), item::getEnchantability, ItemEnchantmentsRegistry::getEnchantability);
    }

    @Redirect(method = "generateEnchantments",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getEnchantability()I"))
    private static int generateCustomEnchantability(Item item) {
        return GlobalUtils.runOrSupply(ChocoEnchantments.getRegistry(item), item::getEnchantability, ItemEnchantmentsRegistry::getEnchantability);
    }

}
