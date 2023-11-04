package net.orandja.strawberry.mods.moretools;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class CustomToolMaterial implements ToolMaterial {
    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;

    private BiPredicate<Enchantment, ItemStack> enchantingCheck = Enchantment::isAcceptableItem;

    public CustomToolMaterial(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient) {
        this.miningLevel = miningLevel;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurability() {
        return this.itemDurability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    public CustomToolMaterial setEnchantingCheck(BiPredicate<Enchantment, ItemStack> predicate) {
        this.enchantingCheck = predicate;
        return this;
    }

    public boolean checkForEnchantment(Enchantment enchantment, ItemStack itemStack) {
        return this.enchantingCheck.test(enchantment, itemStack);
    }
}