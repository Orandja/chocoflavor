//package net.orandja.chocoflavor.mods.core;
//
//import com.google.common.collect.Maps;
//import net.minecraft.enchantment.Enchantment;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//import java.util.Arrays;
//import java.util.Map;
//
//public interface EnchantMore {
//
//    interface EnchantmentPredicate {
//        boolean test(Enchantment enchantment, ItemStack stack);
//    }
//
//    Map<Item, EnchantmentPredicate> ENCHANTMENTS = Maps.newHashMap();
//
//    static boolean acceptsEnchantment(ItemStack stack, Enchantment enchantment) {
//        return ENCHANTMENTS.containsKey(stack.getItem()) && ENCHANTMENTS.get(stack.getItem()).test(enchantment, stack);
//    }
//
//    static void addBasic(Item item, Enchantment... enchantments) {
//        addBasic(item, 1, enchantments);
//    }
//
//    static void addBasic(Item item, int itemCountLimit, Enchantment... enchantments) {
//        ENCHANTMENTS.put(item, (enchantment, stack) -> stack.getCount() <= itemCountLimit && Arrays.asList(enchantments).contains(enchantment));
//    }
//
//    static void addComplex(Item item, EnchantmentPredicate predicate) {
//        ENCHANTMENTS.put(item, predicate);
//    }
//
//    default void itemAcceptsEnchantment(Object enchantmentO, ItemStack stack, CallbackInfoReturnable<Boolean> info) {
//        if(enchantmentO instanceof Enchantment enchantment && acceptsEnchantment(stack, enchantment)) {
//            info.setReturnValue(true);
//        }
//    }
//
//}
