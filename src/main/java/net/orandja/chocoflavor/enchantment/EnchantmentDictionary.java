package net.orandja.chocoflavor.enchantment;

import com.google.common.collect.Maps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Pair;
import net.orandja.chocoflavor.utils.NBTUtils;
import net.orandja.chocoflavor.utils.GlobalUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class EnchantmentDictionary {

    public void getApplied(BiConsumer<Enchantment, Short> consumer) {
        this.values.forEach(consumer);
    }

    public void getApplied(GlobalUtils.TriConsumer<Enchantment, Short, Integer> consumer) {
        AtomicInteger i = new AtomicInteger();
        this.values.forEach((enchantment, level) -> consumer.accept(enchantment, level, i.getAndIncrement()));
    }

    public interface DictionaryCompute<T extends Number> {
        T computeValue(int value);
    }

    public interface DictionaryDoubleCompute {
        double computeValue(int value);
    }

    private final Map<Enchantment, Short> values = Maps.newHashMap();
    private final List<Enchantment> enchantments;

    public EnchantmentDictionary(Enchantment... enchantments) {
        this.enchantments = Arrays.asList(enchantments);
    }

    public boolean hasEnchantment(Enchantment enchantment) {
        return this.hasEnchantment(enchantment, (short) 1);
    }

    public boolean hasEnchantment(Enchantment enchantment, short level) {
        return this.values.entrySet().stream().anyMatch(entry -> entry.getKey().equals(enchantment) && entry.getValue() >= level);
    }

    public boolean hasAnyEnchantment(Enchantment[] filter) {
        return Arrays.stream(filter).anyMatch(this::hasEnchantment);
    }

    public boolean hasAnyEnchantment(EnchantmentArraySetting filter) {
        return Arrays.stream(filter.getValue()).anyMatch(this::hasEnchantment);
    }

    public boolean hasEnchantments() {
        return this.values.size() > 0;
    }

    public short getValue(Enchantment enchantment) {
        if(values.containsKey(enchantment)) {
            return values.get(enchantment);
        }

        return 0;
    }

    public <T extends Number> int computeValue(Enchantment enchantment, EnchantmentDictionary.DictionaryCompute<T> compute) {
        int value = getValue(enchantment);
        return value > 0 ? compute.computeValue(value).intValue() : value;
    }

    public int getValue(Enchantment... enchantments) {
        return Arrays.stream(enchantments).map(this::getValue).mapToInt(it -> it).sum();
    }

    public <T extends Number> int computeValue(EnchantmentDictionary.DictionaryCompute<T> compute, Enchantment... enchantments) {
        int value = getValue(enchantments);
        return value > 0 ? compute.computeValue(value).intValue() : value;
    }

    public EnchantmentDictionary setValue(Enchantment enchantment, short value) {
        if(this.isAllowed(enchantment)) {
            this.values.put(enchantment, value);
        }

        return this;
    }

    public EnchantmentDictionary setValue(Pair<Enchantment, Short> pair) {
        return this.setValue(pair.getLeft(), pair.getRight());
    }

    public EnchantmentDictionary setValue(NbtElement element) {
        if(element instanceof NbtCompound tag) {
            this.setValue(fromTag(tag));
        }
        return this;
    }

    public boolean isAllowed(Enchantment enchantment) {
        return this.enchantments.contains(enchantment);
    }

    private static Pair<Enchantment, Short> fromTag(NbtCompound tag) {
        return new Pair<>(Registries.ENCHANTMENT.get(EnchantmentHelper.getIdFromNbt(tag)), (short) EnchantmentHelper.getLevelFromNbt(tag));
    }

    private static NbtCompound toTag(Map.Entry<Enchantment, Short> entry) {
        NbtCompound tag = new NbtCompound();
        tag.putString("id", EnchantmentHelper.getEnchantmentId(entry.getKey()).toString());
        tag.putShort("lvl", entry.getValue());
        return tag;
    }

    public EnchantmentDictionary loadFromNbt(NbtCompound tag) {
        tag.getList(ItemStack.ENCHANTMENTS_KEY, NbtElement.COMPOUND_TYPE).forEach(this::setValue);
        return this;
    }

    public EnchantmentDictionary saveToNbt(NbtCompound tag) {
        if(this.hasEnchantments()) {
            NbtList saveList = NBTUtils.toNbtList(this.values.entrySet().stream().filter(entry -> entry.getValue() > 0).map(EnchantmentDictionary::toTag));
            if(tag.contains(ItemStack.ENCHANTMENTS_KEY)) {
                tag.getList(ItemStack.ENCHANTMENTS_KEY, NbtElement.COMPOUND_TYPE).addAll(saveList);
            } else {
                tag.put("Enchantments", saveList);
            }
        }

        return this;
    }

    public void saveToNbt(ItemStack it) {
        if(this.hasEnchantments()) {
            saveToNbt(it.getOrCreateNbt());
        }
    }
}
