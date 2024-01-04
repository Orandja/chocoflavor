package net.orandja.chocoflavor;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.orandja.chocoflavor.enchantment.EnchantmentArraySetting;
import net.orandja.chocoflavor.enchantment.EnchantmentDictionary;
import net.orandja.chocoflavor.enchantment.EnchantmentDictionaryUtils;
import net.orandja.chocoflavor.utils.Settings;

public class ChocoBrewingStands {
    private ChocoBrewingStands() {}

    public static void init() {
        ChocoEnchantments.createRegistry(Items.BREWING_STAND)
                .allowInAnvil(VALID_ENCHANTMENTS);

        ChocoWorlds.HOPPER_CANNOT_EXTRACT.add(ChocoBrewingStands::preventExtract);
    }

    public static final EnchantmentArraySetting SPEED = new EnchantmentArraySetting("brewingstand.speed.enchantments", new Enchantment[] { Enchantments.EFFICIENCY, Enchantments.BANE_OF_ARTHROPODS });
    public static final Settings.Number<Double> SPEED_COEF = new Settings.Number<>("brewingstand.speed.coef", 2.0D, Double::parseDouble);

    public static final EnchantmentArraySetting FUEL = new EnchantmentArraySetting("brewingstand.fuel.enchantments", new Enchantment[] { Enchantments.UNBREAKING, Enchantments.FIRE_ASPECT });
    public static final Settings.Number<Double> FUEL_COEF = new Settings.Number<>("brewingstand.fuel.coef", 0.2D, Double::parseDouble);

    public static final EnchantmentArraySetting OUTPUT = new EnchantmentArraySetting("brewingstand.output.enchantments", new Enchantment[] { Enchantments.SILK_TOUCH });

    public static final Enchantment[] VALID_ENCHANTMENTS = EnchantmentDictionaryUtils.concat(SPEED, FUEL, OUTPUT);

    private static boolean preventExtract(World world, BlockPos pos) {
        return EnchantmentDictionaryUtils.getValue(world.getBlockEntity(pos), Handler.class, OUTPUT.getValue(), it -> !it.getInventory().get(3).isEmpty(), false);
    }

    public interface Handler extends ChocoEnchantments.BlockHandler {
        default EnchantmentDictionary createDictionary() {
            return new EnchantmentDictionary(VALID_ENCHANTMENTS);
        }

        int getBrewTime();
        void setBrewTime(int value);

        default void addFuel() {
            this.setFuel(this.getFuel() + this.getDictionary().computeValue(lvl -> lvl * this.getFuel() * ChocoBrewingStands.FUEL_COEF.getValue(), ChocoBrewingStands.FUEL.getValue()));
        }

        default void accelerate() {
            this.setBrewTime(MathHelper.clamp(
                    this.getBrewTime() + 1 - Math.max(1, this.getDictionary().computeValue(lvl -> lvl * SPEED_COEF.getValue(), SPEED.getValue())),
                    0, 400
            ));
        }

        int getFuel();
        void setFuel(int value);

        DefaultedList<ItemStack> getInventory();
    }
}
