//package net.orandja.chocoflavor.mods.blockswithenchantments;
//
//import net.minecraft.enchantment.Enchantment;
//import net.minecraft.enchantment.Enchantments;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.Items;
//import net.minecraft.util.collection.DefaultedList;
//import net.orandja.chocoflavor.mods.core.BlockWithEnchantment;
//import net.orandja.chocoflavor.mods.core.EnchantMore;
//import net.orandja.chocoflavor.utils.Settings;
//
//public interface FurnaceWithEnchantment extends BlockWithEnchantment {
//
//
////    Enchantment[] SPEED = new Enchantment[] { Enchantments.EFFICIENCY, Enchantments.SMITE };
//    EnchantmentArraySetting SPEED = new EnchantmentArraySetting("furnace.speed.enchantments", new Enchantment[] { Enchantments.EFFICIENCY, Enchantments.SMITE });
//    Settings.Number<Double> SPEED_COEF = new Settings.Number<>("furnace.speed.coef", 2D, Double::parseDouble);
//
////    Enchantment[] UNDYING_FUEL = new Enchantment[] { Enchantments.FLAME };
//    EnchantmentArraySetting UNDYING_FUEL = new EnchantmentArraySetting("furnace.undying_fuel.enchantments", new Enchantment[] { Enchantments.FLAME });
//
////    Enchantment[] FUEL = new Enchantment[] { Enchantments.UNBREAKING, Enchantments.FIRE_ASPECT };
//    EnchantmentArraySetting FUEL = new EnchantmentArraySetting("furnace.fuel.enchantments", new Enchantment[] { Enchantments.UNBREAKING, Enchantments.FIRE_ASPECT  });
//    Settings.Number<Double> FUEL_COEF = new Settings.Number<>("furnace.fuel.coef", 0.2D, Double::parseDouble);
//
////    Enchantment[] OUTPUT = new Enchantment[] { Enchantments.FORTUNE };
//    EnchantmentArraySetting FORTUNE = new EnchantmentArraySetting("furnace.fortune.enchantments", new Enchantment[] { Enchantments.FORTUNE });
//
//    Enchantment[] VALID_ENCHANTMENTS = BlockWithEnchantment.concat(SPEED.getValue(), FUEL.getValue(), UNDYING_FUEL.getValue(), FORTUNE.getValue());
//
//    static void beforeLaunch() {
//        EnchantMore.addBasic(Items.FURNACE, VALID_ENCHANTMENTS);
//        EnchantMore.addBasic(Items.SMOKER, VALID_ENCHANTMENTS);
//        EnchantMore.addBasic(Items.BLAST_FURNACE, VALID_ENCHANTMENTS);
//    }
//
//    DefaultedList<ItemStack> getInventory();
//
//    int getCookTime();
//    void vw$setCookTime(int value);
//
//    int getBurnTime();
//    void setBurnTime(int value);
//    int getCookTimeTotal();
//    boolean vw$burning();
//}
