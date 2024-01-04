package net.orandja.chocoflavor.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.orandja.chocoflavor.utils.Settings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class EnchantmentMapSetting extends Settings.Custom<Map<Enchantment, Integer>> {

    public EnchantmentMapSetting(String path, Map<Enchantment, Integer> defaultValue) {
        super(path, defaultValue, EnchantmentMapSetting::serialize, EnchantmentMapSetting::deserializeEnchantments);
    }

    static String serialize(Map<Enchantment, Integer> enchantments) {
        return enchantments.entrySet().stream().map(entry -> Registries.ENCHANTMENT.getId(entry.getKey()).toString() + ":" + entry.getValue()).collect(Collectors.joining(","));
    }

    static Map<Enchantment, Integer> deserializeEnchantments(String value) {
        return new HashMap<Enchantment, Integer>() {{
            Arrays.stream(value.split(",")).map(it -> it.split(":")).filter(it -> it.length == 2).forEach(it -> {
                try {
                    this.put(Registries.ENCHANTMENT.get(new Identifier(it[0])), Integer.parseInt(it[1]));
                } catch (Exception e) {

                }
            });
        }};
    }

    public int getValue(Enchantment enchantment) {
        return this.value.getOrDefault(enchantment, enchantment.getMaxLevel());
    }
}
