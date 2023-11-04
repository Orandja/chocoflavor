package net.orandja.chocoflavor.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ReflectUtils {

    public static <T> void getDeclaredField(Class<?> clazz, Object obj, Predicate<Field> predicate, Consumer<T> consumer) {
        getFieldFrom(clazz.getDeclaredFields(), obj, predicate, consumer);
    }

    public static <T> void getDeclaredField(Object obj, Predicate<Field> predicate, Consumer<T> consumer) {
        getFieldFrom(obj.getClass().getDeclaredFields(), obj, predicate, consumer);
    }

    private static <T> void getFieldFrom(Field[] fields, Object obj, Predicate<Field> predicate, Consumer<T> consumer) {
        for (Field field : fields) {
            if(predicate.test(field)) {
                try {
                    field.setAccessible(true);
                    consumer.accept((T) field.get(obj));
                } catch (Exception e) {
                    Utils.log(e);
                }
            }
        }
    }

    public static <T> void getAllField(Object obj, Predicate<Field> predicate, Consumer<T> consumer) {
        getFieldFrom(obj.getClass().getFields(), obj, predicate, consumer);
        getFieldFrom(obj.getClass().getDeclaredFields(), obj, predicate, consumer);
    }

    public static <T> void getField(Object obj, Predicate<Field> predicate, Consumer<T> consumer) {
        getFieldFrom(obj.getClass().getFields(), obj, predicate, consumer);
    }

    public static <T> void getField(Class<?> clazz, Object obj, Predicate<Field> predicate, Consumer<T> consumer) {
        getFieldFrom(clazz.getFields(), obj, predicate, consumer);
    }

    public static <T> void setDeclaredField(Object obj, Predicate<Field> predicate, Supplier<T> supplier) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            if(predicate.test(field)) {
                try {
                    field.setAccessible(true);
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                    field.set(obj, supplier.get());
                } catch (Exception e) {}
            }
            return;
        }
    }

}