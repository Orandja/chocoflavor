package net.orandja.strawberry.mods.moretools;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.orandja.chocoflavor.utils.NBTUtils;
import net.orandja.chocoflavor.utils.StackUtils;
import net.orandja.chocoflavor.utils.TextUtils;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
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

    public void modifyStack(ItemStack stack, ToolItem replacementItem) {
        stack.setDamage(StackUtils.convertDurability(stack, replacementItem));
        stack.addHideFlag(ItemStack.TooltipSection.MODIFIERS);
        TextUtils.addDurability(stack.getOrCreateNbt(), stack.getDamage(), stack.getMaxDamage());
    }
}