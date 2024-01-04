//package net.orandja.strawberry.mods.core.gui;
//
//import net.minecraft.screen.ScreenTexts;
//import net.minecraft.text.MutableText;
//import net.minecraft.text.Text;
//import net.orandja.chocoflavor.mods.core.BlockWithEnchantment;
//import net.orandja.chocoflavor.utils.GlobalUtils;
//
//public interface SideGUIEnchantments extends SideGUI {
//
//    @Override
//    default boolean isEnabled(Object instance) {
//        return instance instanceof BlockWithEnchantment blockWithEnchantment && blockWithEnchantment.getEnchantmentDictionary().hasEnchantments();
//    }
//
//    @Override
//    default void content(Object instance, MutableText newTitle, Text guiName) {
//        GlobalUtils.apply(Text.literal(""), it -> {
//            ExtraGui.appendTranslatableTo(it, true, "container.enchantments");
//            if(instance instanceof BlockWithEnchantment blockWithEnchantment) {
//                blockWithEnchantment.getEnchantmentDictionary().getApplied((enchantment, level, index) -> {
//                    if(level > 1) {
//                        ExtraGui.appendTranslatableTo(it, index + 2, true,
//                                "container.list.symbol",
//                                ScreenTexts.SPACE,
//                                enchantment.getTranslationKey(),
//                                ScreenTexts.SPACE,
//                                "enchantment.level." + level
//                        );
//                    } else {
//                        ExtraGui.appendTranslatableTo(it, index + 2, true,
//                                "container.list.symbol",
//                                ScreenTexts.SPACE,
//                                enchantment.getTranslationKey()
//                        );
//                    }
//                });
//            }
//        }, newTitle::append);
//    }
//}
