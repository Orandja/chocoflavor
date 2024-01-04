//package net.orandja.chocoflavor.mods.higherenchantmentlevel;
//
//import net.minecraft.enchantment.Enchantment;
//import net.minecraft.enchantment.Enchantments;
//import net.minecraft.registry.Registries;
//import net.minecraft.util.Identifier;
//import net.orandja.chocoflavor.utils.Settings;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
//public interface HigherEnchantmentLevel {
//
//    class EnchantmentMapSetting extends Settings.Custom<Map<Enchantment, Integer>> {
//
//        public EnchantmentMapSetting(String path, Map<Enchantment, Integer> defaultValue) {
//            super(path, defaultValue, EnchantmentMapSetting::serialize, EnchantmentMapSetting::deserializeEnchantments);
//        }
//
//        static String serialize(Map<Enchantment, Integer> enchantments) {
//            return enchantments.entrySet().stream().map(entry -> Registries.ENCHANTMENT.getId(entry.getKey()).toString() + ":" + entry.getValue()).collect(Collectors.joining(","));
//        }
//
//        static Map<Enchantment, Integer> deserializeEnchantments(String value) {
//            return new HashMap<Enchantment, Integer>() {{
//                Arrays.stream(value.split(",")).map(it -> it.split(":")).filter(it -> it.length == 2).forEach(it -> {
//                    try {
//                        this.put(Registries.ENCHANTMENT.get(new Identifier(it[0])), Integer.parseInt(it[1]));
//                    } catch(Exception e) {
//
//                    }
//                });
//            }};
//        }
//
//        public int getValue(Enchantment enchantment) {
//            return this.value.getOrDefault(enchantment, enchantment.getMaxLevel());
//        }
//    }
//
//    EnchantmentMapSetting maxLevels = new EnchantmentMapSetting("higherenchantmentlevels", new HashMap<>() {{
//        this.put(Enchantments.BANE_OF_ARTHROPODS, 10);
//        this.put(Enchantments.BLAST_PROTECTION, 10);
//        this.put(Enchantments.DEPTH_STRIDER, 10);
//        this.put(Enchantments.EFFICIENCY, 10);
//        this.put(Enchantments.FEATHER_FALLING, 10);
//        this.put(Enchantments.FIRE_ASPECT, 10);
//        this.put(Enchantments.FIRE_PROTECTION, 10);
//        this.put(Enchantments.FORTUNE, 10);
//        this.put(Enchantments.FROST_WALKER, 10);
//        this.put(Enchantments.IMPALING, 10);
//        this.put(Enchantments.KNOCKBACK, 10);
//        this.put(Enchantments.LOOTING, 10);
//        this.put(Enchantments.LOYALTY, 10);
//        this.put(Enchantments.LUCK_OF_THE_SEA, 10);
//        this.put(Enchantments.LURE, 10);
//        this.put(Enchantments.MENDING, 10);
//        this.put(Enchantments.PIERCING, 10);
//        this.put(Enchantments.POWER, 10);
//        this.put(Enchantments.PROJECTILE_PROTECTION, 10);
//        this.put(Enchantments.PROTECTION, 10);
//        this.put(Enchantments.PUNCH, 10);
//        this.put(Enchantments.QUICK_CHARGE, 10);
//        this.put(Enchantments.RESPIRATION, 10);
//        this.put(Enchantments.RIPTIDE, 10);
//        this.put(Enchantments.SHARPNESS, 10);
//        this.put(Enchantments.SMITE, 10);
//        this.put(Enchantments.SOUL_SPEED, 10);
//        this.put(Enchantments.SWEEPING, 10);
//        this.put(Enchantments.SWIFT_SNEAK, 10);
//        this.put(Enchantments.THORNS, 10);
//        this.put(Enchantments.UNBREAKING, 10);
//    }});
//
//    default int getMaxLevel(Enchantment enchantment) {
//        return maxLevels.getValue(enchantment);
//    }
//}
