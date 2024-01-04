package net.orandja.chocoflavor.enchantment;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.orandja.chocoflavor.utils.GlobalUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ItemEnchantmentsRegistry {

    public Item item;
    @Getter @Setter @Accessors(chain=true) public int enchantability;
    public BiPredicate<ItemStack, Enchantment> anvilPredicate;
    public BiPredicate<ItemStack, Enchantment> tablePredicate;

    public ItemEnchantmentsRegistry(Item item) {
        this.item = item;
    }

//    public ItemEnchantmentsRegistry setEnchantability(int enchantability) {
//        this.enchantability = enchantability;
//        return this;
//    }

    public ItemEnchantmentsRegistry allowInTable(Enchantment... enchantments) {
        List<Enchantment> list = Arrays.asList(enchantments);
        return this.allowInTable((stack, enchantment) -> list.contains(enchantment));
    }

    public ItemEnchantmentsRegistry allowInTable(BiPredicate<ItemStack, Enchantment> tablePredicate) {
        this.tablePredicate = tablePredicate;
        return this;
    }

    public boolean isAllowedInTable(ItemStack stack, Enchantment enchantment) {
        return GlobalUtils.runOrDefault(tablePredicate, false, it -> it.test(stack, enchantment));
    }

    public ItemEnchantmentsRegistry allowInAnvil(Enchantment... enchantments) {
        List<Enchantment> list = Arrays.asList(enchantments);
        return this.allowInAnvil((stack, enchantment) -> list.contains(enchantment) || enchantment.isAcceptableItem(stack));
    }

    public ItemEnchantmentsRegistry allowInAnvil(BiPredicate<ItemStack, Enchantment> anvilPredicate) {
        this.anvilPredicate = anvilPredicate;
        return this;
    }

    public boolean isAllowedInAnvil(ItemStack stack, Enchantment enchantment) {
        return GlobalUtils.runOrDefault(anvilPredicate, false, it -> it.test(stack, enchantment));
    }

    public Boolean isTableEnchantable() {
        return this.tablePredicate != null;
    }

    public Boolean isAnvilEnchantable() {
        return this.anvilPredicate != null;
    }

}
