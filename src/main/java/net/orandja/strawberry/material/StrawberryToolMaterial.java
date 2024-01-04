package net.orandja.strawberry.material;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.orandja.chocoflavor.utils.StackUtils;
import net.orandja.chocoflavor.utils.TextUtils;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class StrawberryToolMaterial implements ToolMaterial {
    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;

    private BiPredicate<Enchantment, ItemStack> enchantingCheck = Enchantment::isAcceptableItem;

    public StrawberryToolMaterial(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient) {
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

    public StrawberryToolMaterial setEnchantingCheck(BiPredicate<Enchantment, ItemStack> predicate) {
        this.enchantingCheck = predicate;
        return this;
    }

    public boolean checkForEnchantment(Enchantment enchantment, ItemStack itemStack) {
        return this.enchantingCheck.test(enchantment, itemStack);
    }

    public void modifyStack(ItemStack newStack, ItemStack sourceStack, Item replacementItem) {
        modifyStack(newStack, sourceStack, replacementItem.getMaxDamage());
    }

    public void modifyStack(ItemStack newStack, ItemStack sourceStack, int maxDamage) {
        newStack.setDamage(StackUtils.convertDurability(sourceStack, maxDamage));
        newStack.addHideFlag(ItemStack.TooltipSection.MODIFIERS);
        TextUtils.addDurability(newStack.getOrCreateNbt(), sourceStack.getDamage(), sourceStack.getMaxDamage());
    }
}