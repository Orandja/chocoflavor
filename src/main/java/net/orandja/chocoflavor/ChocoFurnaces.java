package net.orandja.chocoflavor;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.orandja.chocoflavor.enchantment.EnchantmentArraySetting;
import net.orandja.chocoflavor.enchantment.EnchantmentDictionary;
import net.orandja.chocoflavor.enchantment.EnchantmentDictionaryUtils;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.chocoflavor.utils.Settings;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ChocoFurnaces {
    private ChocoFurnaces() {}

    public static void init() {
        ChocoEnchantments.createRegistry(Items.FURNACE)
                .setEnchantability(22)
                .allowInTable(VALID_ENCHANTMENTS)
                .allowInAnvil(VALID_ENCHANTMENTS);

        ChocoEnchantments.createRegistry(Items.SMOKER)
                .setEnchantability(22)
                .allowInTable(VALID_ENCHANTMENTS)
                .allowInAnvil(VALID_ENCHANTMENTS);

        ChocoEnchantments.createRegistry(Items.BLAST_FURNACE)
                .setEnchantability(22)
                .allowInTable(VALID_ENCHANTMENTS)
                .allowInAnvil(VALID_ENCHANTMENTS);
    }

    public static final EnchantmentArraySetting SPEED = new EnchantmentArraySetting("furnace.speed.enchantments", new Enchantment[] { Enchantments.EFFICIENCY, Enchantments.SMITE });
    public static final Settings.Number<Double> SPEED_COEF = new Settings.Number<>("furnace.speed.coef", 2D, Double::parseDouble);

    public static final EnchantmentArraySetting UNDYING_FUEL = new EnchantmentArraySetting("furnace.undying_fuel.enchantments", new Enchantment[] { Enchantments.FLAME });

    public static final EnchantmentArraySetting FUEL = new EnchantmentArraySetting("furnace.fuel.enchantments", new Enchantment[] { Enchantments.UNBREAKING, Enchantments.FIRE_ASPECT  });
    public static final Settings.Number<Double> FUEL_COEF = new Settings.Number<>("furnace.fuel.coef", 0.2D, Double::parseDouble);

    public static final EnchantmentArraySetting FORTUNE = new EnchantmentArraySetting("furnace.fortune.enchantments", new Enchantment[] { Enchantments.FORTUNE });

    public static final Enchantment[] VALID_ENCHANTMENTS = EnchantmentDictionaryUtils.concat(SPEED.getValue(), FUEL.getValue(), UNDYING_FUEL.getValue(), FORTUNE.getValue());

    public interface Handler extends ChocoEnchantments.BlockHandler {
        default EnchantmentDictionary createDictionary() {
            return new EnchantmentDictionary(VALID_ENCHANTMENTS);
        }
        DefaultedList<ItemStack> getInventory();

        int getCookTime();
        void setCookTime(int value);

        World getWorld();

        int getBurnTime();
        void setBurnTime(int value);
        int getCookTimeTotal();
        boolean furnaceBurning();

        default void decreaseBurnTime() {
            boolean hasUndyingFuel = getDictionary().hasAnyEnchantment(UNDYING_FUEL.getValue());
            boolean slotEmpty = getInventory().get(0).isEmpty();

            if(furnaceBurning() && (!hasUndyingFuel || !slotEmpty)) {
                setBurnTime(Math.max(0, getBurnTime() - Math.max(1, getDictionary().computeValue(lvl -> lvl * SPEED_COEF.getValue(), SPEED.getValue()))));
            }
        }

        default void addMoreBurnTime(CallbackInfoReturnable<Integer> info) {
            info.setReturnValue((int) (info.getReturnValue() * (1 + (getDictionary().getValue(FUEL.getValue()) * FUEL_COEF.getValue()))));
        }

        default void accelerate() {
            setCookTime(getCookTime() - 1 + Math.max(1, getDictionary().getValue(SPEED.getValue()) * 2));
        }

        default void increaseOutput() {
            if(!(getInventory().get(2).getItem() instanceof BlockItem)) {
                getInventory().get(2).increment(Math.max(0, getDictionary().computeValue(lvl -> getWorld().getRandom().nextInt(lvl + 2), FORTUNE.getValue()) - 1));
            }
        }
    }
}
