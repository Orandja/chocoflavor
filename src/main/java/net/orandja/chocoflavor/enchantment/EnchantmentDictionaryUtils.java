package net.orandja.chocoflavor.enchantment;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.Enchantment;
import net.orandja.chocoflavor.ChocoEnchantments;
import net.orandja.chocoflavor.utils.GlobalUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EnchantmentDictionaryUtils {
    public static Enchantment[] concat(Enchantment[]... enchantments) {
        List<Enchantment> list = Lists.newArrayList();
        for (Enchantment[] enchantmentArray : enchantments) {
            list.addAll(Arrays.asList(enchantmentArray));
        }
        return list.toArray(Enchantment[]::new);
    }

    public static Enchantment[] concat(EnchantmentArraySetting... enchantments) {
        List<Enchantment> list = Lists.newArrayList();
        for (EnchantmentArraySetting enchantmentArray : enchantments) {
            list.addAll(Arrays.asList(enchantmentArray.getValue()));
        }
        return list.toArray(Enchantment[]::new);
    }

    public static <T extends ChocoEnchantments.BlockHandler> void compute(Object object, Class<T> clazz, Consumer<T> consumer) {
        GlobalUtils.applyAs(object, clazz, consumer);
    }

    public static <T extends ChocoEnchantments.BlockHandler> void compute(Object object, Class<T> clazz, Enchantment[] enchantments, Consumer<T> consumer) {
        GlobalUtils.applyAs(object, clazz, it -> {
            if (it.getDictionary().hasAnyEnchantment(enchantments)) consumer.accept(it);
        });
    }

    public static <T extends ChocoEnchantments.BlockHandler> void computeWithValue(Object object, Class<T> clazz, Enchantment[] enchantments, BiConsumer<T, Integer> consumer) {
        GlobalUtils.applyAs(object, clazz, it -> {
            if (it.getDictionary().hasAnyEnchantment(enchantments)) {
                consumer.accept(it, it.getDictionary().getValue(enchantments));
            }
        });
    }

    public static <T extends ChocoEnchantments.BlockHandler, V> V getValue(Object object, Class<T> clazz, Enchantment[] enchantments, GlobalUtils.CSupplier<T, V> supplier, V defaultValue) {
        return GlobalUtils.runAsWithDefault(object, clazz, defaultValue, it -> it.getDictionary().hasAnyEnchantment(enchantments) ?
                supplier.getValue(it) :
                defaultValue);
    }

    public static <T extends ChocoEnchantments.BlockHandler, V> V getValue(Object object, Class<T> clazz, Enchantment[] enchantments, GlobalUtils.CBiSupplier<V, T, Integer> supplier, V defaultValue) {
        return GlobalUtils.runAsWithDefault(object, clazz, defaultValue, it -> it.getDictionary().hasAnyEnchantment(enchantments) ?
                supplier.getValue(it, it.getDictionary().getValue(enchantments)) :
                defaultValue);
    }
}
