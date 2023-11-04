package net.orandja.chocoflavor.mods.tools;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.orandja.chocoflavor.mods.core.EnchantMore;

public interface Tools {

    static void beforeLaunch() {
        EnchantMore.addBasic(Items.STICK, Enchantments.KNOCKBACK);
        EnchantMore.addBasic(Items.SHEARS, Enchantments.FORTUNE);
    }
}
