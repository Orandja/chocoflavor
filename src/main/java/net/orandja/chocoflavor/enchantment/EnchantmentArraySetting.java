package net.orandja.chocoflavor.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.orandja.chocoflavor.utils.Settings;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EnchantmentArraySetting extends Settings.Custom<Enchantment[]> {

    public EnchantmentArraySetting(String path, Enchantment[] defaultValue) {
        super(path, defaultValue, EnchantmentArraySetting::serialize, EnchantmentArraySetting::deserializeEnchantments);
    }

    static String serialize(Enchantment[] enchantments) {
        return Arrays.stream(enchantments).map(it -> Registries.ENCHANTMENT.getId(it).toString()).collect(Collectors.joining(","));
    }

    static Enchantment[] deserializeEnchantments(String value) {
        return Arrays.stream(value.split(",")).map(Identifier::new).map(Registries.ENCHANTMENT::get).toArray(Enchantment[]::new);
    }

    public boolean contains(Enchantment enchantment) {
        return Arrays.asList(this.value).contains(enchantment);
    }

    public boolean anyMatch(Predicate<Enchantment> predicate) {
        return Arrays.asList(this.value).stream().anyMatch(predicate);
    }

    public void computeWithValue(ItemStack stack, Consumer<Integer> consumer) {
        AtomicInteger v = new AtomicInteger(0);
        Arrays.asList(this.value).stream().map(it -> EnchantmentHelper.getLevel(it, stack)).filter(it -> it > 0).forEach(it -> {
            v.set(v.get() + it);
        });
        if (v.get() > 0) {
            consumer.accept(v.get());
        }
    }
}
