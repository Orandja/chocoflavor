package net.orandja.chocoflavor.enchantment;

import lombok.Getter;
import net.minecraft.enchantment.Enchantment;

public class EnchantmentLevelRegistry {

    @Getter private final Enchantment enchantment;
    @Getter private int maxLevel;
    @Getter private int minLevel;
    @Getter private int maxAnvilLevel;
    @Getter private int minAnvilLevel;

    public EnchantmentLevelRegistry(Enchantment enchantment) {
        this.enchantment = enchantment;
        this.maxLevel = enchantment.getMaxLevel();
        this.minLevel = enchantment.getMinLevel();
        this.maxAnvilLevel = this.maxLevel;
        this.minAnvilLevel = this.minLevel;
    }

    public EnchantmentLevelRegistry setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }

    public EnchantmentLevelRegistry setMinLevel(int minLevel) {
        this.minLevel = minLevel;
        return this;
    }

    public EnchantmentLevelRegistry setMaxAnvilLevel(int maxAnvilLevel) {
        this.maxAnvilLevel = maxAnvilLevel;
        return this;
    }

    public EnchantmentLevelRegistry setMinAnvilLevel(int minAnvilLevel) {
        this.minAnvilLevel = minAnvilLevel;
        return this;
    }

}
