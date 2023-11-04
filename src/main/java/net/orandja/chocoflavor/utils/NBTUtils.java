package net.orandja.chocoflavor.utils;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class NBTUtils {

    public static <T, R> void convertToList(String key, int listType, List<T> list, Function<NbtElement, T> mapper, NbtCompound nbt) {
        if(nbt.contains(key)) {
            nbt.getList(key, listType).stream().map(mapper).forEach(list::add);
        }
    }

    public static <T, R> void convertToNBTList(String key, List<T> list, Function<T, NbtElement> mapper, NbtCompound nbt) {
        if(list.size() > 0) {
            NbtList nbtList = new NbtList();
            list.stream().map(mapper).forEach(nbtList::add);
            nbt.put(key, nbtList);
        }
    }

    public static NbtList toNbtList(Stream<NbtElement> stream) {
        return toNbtList(stream.collect(Collectors.toList()));
    }
    public static NbtList toNbtList(List<NbtElement> list) {
        NbtList nbtList = new NbtList();
        nbtList.addAll(list);
        return nbtList;
    }

    public static NbtList addTo(NbtList list, NbtElement element) {
        list.add(element);
        return list;
    }

    public static NbtList addTo(NbtList list, String string) {
        list.add(NbtString.of(string));
        return list;
    }

    public static NbtCompound createBlankCompound(Consumer<NbtCompound> consumer) {
        NbtCompound compound = new NbtCompound();
        consumer.accept(compound);
        return compound;
    }

    public interface NbtSupplier<N> {
        N get(String id);
    }

    public interface NbtCompute<N> {
        N get();
    }

    public static <N extends NbtElement> N getOrCompute(NbtCompound tag, String id, NbtSupplier<N> supplier, NbtCompute<N> compute, Consumer<N> consumer) {
        N value;
        if(tag.contains(id)) {
            value = supplier.get(id);
        } else {
            value = compute.get();
            tag.put(id, value);
        }

        consumer.accept(value);

        return value;
    }

    public static <N extends NbtElement> N getOrCreate(NbtCompound tag, String id, NbtSupplier<N> supplier, NbtCompute<N> compute) {
        if(tag.contains(id)) {
            return (N) tag.get(id);
        } else {
            N value = compute.get();
            tag.put(id, value);
            return value;
        }
    }

    public static <N extends NbtElement> N getOrCreate(NbtCompound tag, String id, NbtCompute<N> compute) {
        if(tag.contains(id)) {
            return (N) tag.get(id);
        } else {
            N value = compute.get();
            tag.put(id, value);
            return value;
        }
    }

    public static NbtCompound getTagOrCompute(NbtCompound tag, String id, Consumer<NbtCompound> consumer) {
        return getOrCompute(tag, id, tag::getCompound, NbtCompound::new, consumer);
    }

    public static NbtList getStringListOrCompute(NbtCompound tag, String id, Consumer<NbtList> consumer) {
        return getStringListOrCompute(tag, id, NbtList::new, consumer);
    }

    public static NbtList getStringListOrCompute(NbtCompound tag, String id, NbtCompute<NbtList> compute, Consumer<NbtList> consumer) {
        return getOrCompute(tag, id, key -> tag.getList(key, NbtElement.STRING_TYPE), compute, consumer);
    }

    public static void computeLore(NbtCompound tag, Consumer<NbtList> consumer) {
        NbtCompound display = NBTUtils.getOrCreate(tag, "display", tag::getCompound, NbtCompound::new);
        NbtList lore = NBTUtils.getOrCreate(display, "Lore", key -> display.getList(key, NbtElement.STRING_TYPE), () -> {
            NbtList list = new NbtList();
            return list;
        });
        consumer.accept(lore);
    }

    public static void addToLore(NbtCompound tag, String... lines) {
        computeLore(tag, lore -> {
            for (String line : lines) {
                lore.add(NbtString.of(line));
            }
        });
    }
}
