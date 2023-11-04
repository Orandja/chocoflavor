package net.orandja.chocoflavor.mods.core;

import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.mods.core.accessor.LootContextParameterSetAccessor;
import net.orandja.chocoflavor.utils.NBTUtils;
import net.orandja.chocoflavor.utils.Settings;
import com.google.common.collect.Lists;
import net.orandja.chocoflavor.utils.TriConsumer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface BlockWithEnchantment {

    interface BlockWithEnchantmentCompute<T extends BlockWithEnchantment> {
        void compute(T blockWithEnchantment);
    }

    interface BlockWithEnchantmentSupplier<T extends BlockWithEnchantment, V> {
        V getValue(T blockWithEnchantment);
    }

    interface BlockWithEnchantmentBiSupplier<T extends BlockWithEnchantment, V> {
        V getValue(T blockWithEnchantment, int lvl);
    }

    class EnchantmentArraySetting extends Settings.Custom<Enchantment[]> {

        public EnchantmentArraySetting(String path, Enchantment[] defaultValue) {
            super(path, defaultValue, EnchantmentArraySetting::serialize, EnchantmentArraySetting::deserializeEnchantments);
        }

        static String serialize(Enchantment[] enchantments) {
            return Arrays.stream(enchantments).map(it -> Registries.ENCHANTMENT.getId(it).toString()).collect(Collectors.joining(","));
        }

        static Enchantment[] deserializeEnchantments(String value) {
            return Arrays.stream(value.split(",")).map(Identifier::new).map(Registries.ENCHANTMENT::get).toArray(Enchantment[]::new);
        }

        public boolean contains(Enchantment enchantment) {
            return Arrays.asList(this.value).contains(enchantment);
        }

        public boolean anyMatch(Predicate<Enchantment> predicate) {
            return Arrays.asList(this.value).stream().anyMatch(predicate);
        }

        public void computeWithValue(ItemStack stack, Consumer<Integer> consumer) {
            AtomicInteger v = new AtomicInteger(0);
            Arrays.asList(this.value).stream().map(it -> EnchantmentHelper.getLevel(it, stack)).filter(it -> it > 0).forEach(it -> {
                v.set(v.get() + it);
            });
            if(v.get() > 0) {
                consumer.accept(v.get());
            }
        }
    }

    static <T extends BlockWithEnchantment> void compute(Object object, Class<T> clazz, Consumer<T> consumer) {
        if(clazz.isInstance(object)) {
            consumer.accept(clazz.cast(object));
        }
    }

    static <T extends BlockWithEnchantment> void compute(Object object, Class<T> clazz, Enchantment[] enchantments, Consumer<T> consumer) {
        if(clazz.isInstance(object)) {
            T t = clazz.cast(object);
            if(t.getEnchantmentDictionary().hasAnyEnchantment(enchantments)) {
                consumer.accept(t);
            }
        }
    }

    static <T extends BlockWithEnchantment> void computeWithValue(Object object, Class<T> clazz, Enchantment[] enchantments, BiConsumer<T, Integer> consumer) {
        if(clazz.isInstance(object)) {
            T t = clazz.cast(object);
            if(t.getEnchantmentDictionary().hasAnyEnchantment(enchantments)) {
                consumer.accept(t, t.getEnchantmentDictionary().getValue(enchantments));
            }
        }
    }

    static <T extends BlockWithEnchantment, V> V getValue(Object object, Class<T> clazz, Enchantment[] enchantments, BlockWithEnchantmentSupplier<T, V> supplier, V defaultValue) {
        if(clazz.isInstance(object) || (object != null && clazz.isAssignableFrom(object.getClass()))) {
            T t = clazz.cast(object);
            if(t.getEnchantmentDictionary().hasAnyEnchantment(enchantments)) {
                return supplier.getValue(t);
            }
        }
        return defaultValue;
    }

    static <T extends BlockWithEnchantment, V> V getValue(Object object, Class<T> clazz, Enchantment[] enchantments, BlockWithEnchantmentBiSupplier<T, V> supplier, V defaultValue) {
        if(clazz.isInstance(object)) {
            T t = clazz.cast(object);
            if(t.getEnchantmentDictionary().hasAnyEnchantment(enchantments)) {
                return supplier.getValue(t, t.getEnchantmentDictionary().getValue(enchantments));
            }
        }
        return defaultValue;
    }

    class EnchantmentDictionary {

        public void getApplied(BiConsumer<Enchantment, Short> consumer) {
            this.values.forEach(consumer);
        }

        public void getApplied(TriConsumer<Enchantment, Short, Integer> consumer) {
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

        public <T extends Number> int computeValue(Enchantment enchantment, DictionaryCompute<T> compute) {
            int value = getValue(enchantment);
            return value > 0 ? compute.computeValue(value).intValue() : value;
        }

        public int getValue(Enchantment... enchantments) {
            return Arrays.stream(enchantments).map(this::getValue).mapToInt(it -> it).sum();
        }

        public <T extends Number> int computeValue(DictionaryCompute<T> compute, Enchantment... enchantments) {
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

    static Enchantment[] concat(Enchantment[]... enchantments) {
        List<Enchantment> list = Lists.newArrayList();
        for (Enchantment[] enchantmentArray : enchantments) {
            list.addAll(Arrays.asList(enchantmentArray));
        }
        return list.toArray(Enchantment[]::new);
    }

    static Enchantment[] concat(EnchantmentArraySetting... enchantments) {
        List<Enchantment> list = Lists.newArrayList();
        for (EnchantmentArraySetting enchantmentArray : enchantments) {
            list.addAll(Arrays.asList(enchantmentArray.getValue()));
        }
        return list.toArray(Enchantment[]::new);
    }

    EnchantmentDictionary getEnchantmentDictionary();

    default void onBlockPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if(stack.hasNbt() && stack.getNbt().contains(ItemStack.ENCHANTMENTS_KEY)) {
            if(state.getBlock() instanceof BlockWithEntity && world.getBlockEntity(pos) instanceof BlockWithEnchantment blockWithEnchantment) {
                blockWithEnchantment.getEnchantmentDictionary().loadFromNbt(stack.getNbt());
                blockWithEnchantment.applyEnchantments();
            }
        }
    }

    default void applyEnchantments() {

    }

    default List<ItemStack> enchantLoots(List<ItemStack> drops, BlockState state, LootContextParameterSet.Builder builder) {
        Map<LootContextParameter<?>, Object> parameters = ((LootContextParameterSetAccessor)builder).getParameters();
        if(parameters != null && parameters.get(LootContextParameters.BLOCK_ENTITY) instanceof BlockWithEnchantment blockWithEnchantment) {
            drops.forEach(blockWithEnchantment.getEnchantmentDictionary()::saveToNbt);
        }

        return drops;
    }
}
