//package net.orandja.chocoflavor.mods.blockswithenchantments;
//
//import net.minecraft.enchantment.Enchantment;
//import net.minecraft.enchantment.Enchantments;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.Items;
//import net.minecraft.util.collection.DefaultedList;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import net.orandja.chocoflavor.mods.core.BlockWithEnchantment;
//import net.orandja.chocoflavor.mods.core.EnchantMore;
//import net.orandja.chocoflavor.mods.core.ProtectBlock;
//import net.orandja.chocoflavor.utils.Settings;
//
//public interface BrewingStand extends BlockWithEnchantment {
//
//    EnchantmentArraySetting SPEED = new EnchantmentArraySetting("brewingstand.speed.enchantments", new Enchantment[] { Enchantments.EFFICIENCY, Enchantments.BANE_OF_ARTHROPODS });
//    Settings.Number<Double> SPEED_COEF = new Settings.Number<>("brewingstand.speed.coef", 2.0D, Double::parseDouble);
//
//    EnchantmentArraySetting FUEL = new EnchantmentArraySetting("brewingstand.fuel.enchantments", new Enchantment[] { Enchantments.UNBREAKING, Enchantments.FIRE_ASPECT });
//    Settings.Number<Double> FUEL_COEF = new Settings.Number<>("brewingstand.fuel.coef", 0.2D, Double::parseDouble);
//
//    EnchantmentArraySetting OUTPUT = new EnchantmentArraySetting("brewingstand.output.enchantments", new Enchantment[] { Enchantments.SILK_TOUCH });
//
//    Enchantment[] VALID_ENCHANTMENTS = BlockWithEnchantment.concat(SPEED, FUEL, OUTPUT);
//
//    static void beforeLaunch() {
//        EnchantMore.addBasic(Items.BREWING_STAND, VALID_ENCHANTMENTS);
//
//        ProtectBlock.HOPPER_CANNOT_EXTRACT.add(BrewingStand::preventExtract);
//    }
//
//    private static boolean preventExtract(World world, BlockPos pos) {
//        return BlockWithEnchantment.getValue(world.getBlockEntity(pos), BrewingStand.class, OUTPUT.getValue(), it -> !it.getInventory().get(3).isEmpty(), false);
//    }
//
//    int getBrewTime();
//    void setBrewTime(int value);
//
//    int getFuel();
//    void setFuel(int value);
//
//    DefaultedList<ItemStack> getInventory();
//}
