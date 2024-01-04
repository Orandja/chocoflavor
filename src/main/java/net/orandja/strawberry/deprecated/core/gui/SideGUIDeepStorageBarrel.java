//package net.orandja.strawberry.mods.core.gui;
//
//import net.minecraft.screen.ScreenTexts;
//import net.minecraft.text.MutableText;
//import net.minecraft.text.Text;
//import net.orandja.chocoflavor.mods.core.BlockWithEnchantment;
//import net.orandja.chocoflavor.mods.blockswithenchantments.DeepStorageBarrel;
//import net.orandja.chocoflavor.utils.StackUtils;
//import net.orandja.chocoflavor.utils.GlobalUtils;
//
//import java.util.concurrent.atomic.AtomicInteger;
//
//
//public interface SideGUIDeepStorageBarrel extends SideGUIEnchantments {
//
//    @Override
//    default void content(Object instance, MutableText newTitle, Text guiName) {
//        GlobalUtils.apply(Text.literal(""), it -> {
//            ExtraGui.appendTranslatableTo(it, true, "container.enchantments");
//            AtomicInteger lastOffset = new AtomicInteger();
//            if(instance instanceof BlockWithEnchantment blockWithEnchantment) {
//                blockWithEnchantment.getEnchantmentDictionary().getApplied((enchantment, level, index) -> {
//                    lastOffset.set(index + 2);
//                    if(level > 1) {
//                        ExtraGui.appendTranslatableTo(it, (index + 2), true,
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
//
//            if(instance instanceof DeepStorageBarrel barrel) {
//                if(!barrel.getInventory().get(0).isEmpty()) {
//                    ExtraGui.appendTranslatableTo(it, lastOffset.get() + 2, true, "container.deepstoragebarrel.contents");
//                    ExtraGui.appendTranslatableTo(it, lastOffset.get() + 3, true,
//                            ScreenTexts.SPACE,
//                            Text.literal(StackUtils.wholeCount(barrel.getInventory()) + ""),
//                            ScreenTexts.SPACE,
//                            barrel.getInventory().get(0).getTranslationKey());
//                }
//            }
//        }, newTitle::append);
//    }
//}
