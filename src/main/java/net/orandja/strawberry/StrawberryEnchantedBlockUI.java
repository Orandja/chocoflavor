package net.orandja.strawberry;

import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.orandja.chocoflavor.ChocoBarrels;
import net.orandja.chocoflavor.ChocoEnchantments;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.chocoflavor.utils.StackUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class StrawberryEnchantedBlockUI {
    private StrawberryEnchantedBlockUI() {}

    public interface EnchantedSideUI extends StrawberryExtraUI.SideUI {

        @Override
        default boolean isEnabled(Object instance) {
            return instance instanceof ChocoEnchantments.BlockHandler handler && handler.getDictionary().hasEnchantments();
        }

        @Override
        default void content(Object instance, MutableText newTitle, Text guiName) {
            GlobalUtils.apply(Text.literal(""), it -> {
                StrawberryExtraUI.GlyphUtils.appendTranslatableTo(it, true, "container.enchantments");
                if(instance instanceof ChocoEnchantments.BlockHandler handler) {
                    handler.getDictionary().getApplied((enchantment, level, index) -> {
                        if(level > 1) {
                            StrawberryExtraUI.GlyphUtils.appendTranslatableTo(it, index + 2, true,
                                    "container.list.symbol",
                                    ScreenTexts.SPACE,
                                    enchantment.getTranslationKey(),
                                    ScreenTexts.SPACE,
                                    "enchantment.level." + level
                            );
                        } else {
                            StrawberryExtraUI.GlyphUtils.appendTranslatableTo(it, index + 2, true,
                                    "container.list.symbol",
                                    ScreenTexts.SPACE,
                                    enchantment.getTranslationKey()
                            );
                        }
                    });
                }
            }, newTitle::append);
        }
    }

    public interface BarrelSideUI extends EnchantedSideUI {

        @Override
        default void content(Object instance, MutableText newTitle, Text guiName) {
            GlobalUtils.apply(Text.literal(""), it -> {
                StrawberryExtraUI.GlyphUtils.appendTranslatableTo(it, true, "container.enchantments");
                AtomicInteger lastOffset = new AtomicInteger();
                if(instance instanceof ChocoEnchantments.BlockHandler handler) {
                    handler.getDictionary().getApplied((enchantment, level, index) -> {
                        lastOffset.set(index + 2);
                        if(level > 1) {
                            StrawberryExtraUI.GlyphUtils.appendTranslatableTo(it, (index + 2), true,
                                    "container.list.symbol",
                                    ScreenTexts.SPACE,
                                    enchantment.getTranslationKey(),
                                    ScreenTexts.SPACE,
                                    "enchantment.level." + level
                            );
                        } else {
                            StrawberryExtraUI.GlyphUtils.appendTranslatableTo(it, index + 2, true,
                                    "container.list.symbol",
                                    ScreenTexts.SPACE,
                                    enchantment.getTranslationKey()
                            );
                        }
                    });
                }

                if(instance instanceof ChocoBarrels.Handler barrel) {
                    if(!barrel.getInventory().get(0).isEmpty()) {
                        StrawberryExtraUI.GlyphUtils.appendTranslatableTo(it, lastOffset.get() + 2, true, "container.deepstoragebarrel.contents");
                        StrawberryExtraUI.GlyphUtils.appendTranslatableTo(it, lastOffset.get() + 3, true,
                                ScreenTexts.SPACE,
                                Text.literal(StackUtils.wholeCount(barrel.getInventory()) + ""),
                                ScreenTexts.SPACE,
                                barrel.getInventory().get(0).getTranslationKey());
                    }
                }
            }, newTitle::append);
        }
    }
}
