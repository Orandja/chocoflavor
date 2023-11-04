package net.orandja.chocoflavor.utils;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.orandja.chocoflavor.ChocoFlavor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TextUtils {

    public static String getNonItalicTranslatable(String key, Object... args) {
        return getNonItalicTranslatable(key, null, args);
    }
    public static String getNonItalicTranslatable(String key, Consumer<MutableText> textConsumer, Object... args) {
        return Text.Serializer.toJson(
                Utils.apply(Text.translatable(key, args), it -> {
                    it.formatted(Formatting.ITALIC).formatted(Formatting.WHITE);
                    if(textConsumer != null)
                        textConsumer.accept(it);
                })
            ).replace("\"italic\":true", "\"italic\":false");
    }

    public static String getNonItalicTranslatable(Text text, Consumer<MutableText> textConsumer, Object... args) {
        return Text.Serializer.toJson(
                Utils.apply(((MutableText) text), it -> {
                    it.formatted(Formatting.ITALIC).formatted(Formatting.WHITE);
                    if(textConsumer != null)
                        textConsumer.accept(it);
                })
        ).replace("\"italic\":true", "\"italic\":false");
    }

    public static void addDurability(NbtCompound tag, int damage, int maxDamage) {
        if(damage > 0)
            NBTUtils.addToLore(tag, TextUtils.getNonItalicTranslatable("item.durability", maxDamage - damage, maxDamage));
    }

    public static void addEnchantments(NbtCompound tag, NbtList enchantments) {
        for (int i = 0; i < enchantments.size(); ++i) {
            NbtCompound nbtCompound = enchantments.getCompound(i);
            Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound)).ifPresent(e -> {
                Utils.apply(TextUtils.getNonItalicTranslatable(e.getName(EnchantmentHelper.getLevelFromNbt(nbtCompound)), translatable -> {
                    translatable.formatted(Formatting.GRAY);
                }), it -> NBTUtils.addToLore(tag, it));
            });
        }
    }
}
