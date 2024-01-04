package net.orandja.chocoflavor.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.orandja.chocoflavor.ChocoFlavor;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class GlobalUtils {

    public interface SupplierBiParameters<A, B, C> {
        A create(B b, C c);
    }

    public interface PairSupplier<A, B, C> {
        Pair<A, B> get(C c);
    }

    public interface ObjectCreator<T> {
        T create() throws Exception;
    }

    public interface ObjectRunner<T, R> {
        R create(T value) throws Exception;
    }

    public interface Runner {
        void run() throws Exception;
    }

    public interface ErrorConsumer<T> {
        void accept(T value) throws Exception;
    }

    public static void justTry(Runner runner) {
        try {
            runner.run();
        } catch(Exception ignore) {}
    }

    public static <T> T tryOrDefault(ObjectCreator<T> supplier, T defaultValue) {
        return tryOrCatch(supplier, null, defaultValue, null);
    }

    public static <T> T tryOrIgnore(ObjectCreator<T> supplier, ErrorConsumer<T> consumer) {
        return tryOrCatch(supplier, consumer, null, null);
    }

    public static <T> T tryOrCatchIgnore(ObjectCreator<T> supplier, ErrorConsumer<T> consumer, T defaultValue) {
        return tryOrCatch(supplier, consumer, defaultValue, null);
    }

    public static <T> T tryOrCatch(ObjectCreator<T> supplier, ErrorConsumer<T> consumer, T defaultValue, Consumer<Exception> errorConsumer) {
        try {
            T value = supplier.create();
            if(consumer != null)
                consumer.accept(value);
            return value;
        } catch(Exception e) {
            if(errorConsumer != null)
                errorConsumer.accept(e);
        }

        return defaultValue;
    }

    public static <T> T create(Supplier<T> creator, Consumer<T> consumer) {
        T object = creator.get();
        consumer.accept(object);
        return object;
    }

    public static <T> T apply(T object, Consumer<T> consumer) {
        if(object != null)
            consumer.accept(object);

        return object;
    }

    @SafeVarargs
    public static <T> T apply(T object, Consumer<T>... consumers) {
        if(object != null) {
            for (Consumer<T> consumer : consumers) {
                consumer.accept(object);
            }
        }

        return object;
    }

    public static <T, R> R run(T object, Function<T, R> runner) {
        return runner.apply(object);
    }

    public static <T, R> R runAndApply(T object, Function<T, R> runner, Consumer<R> consumer) {
        return apply(run(object, runner), consumer);
    }

    public static <T, R> R runOrNull(T object, Function<T, R> runner) {
        return object == null ? null : runner.apply(object);
    }

    public static <T, R> R runOrDefault(T object, R defaultValue, Function<T, R> runner) {
        return object == null ? defaultValue : runner.apply(object);
    }

    public static <T, R> R runOrSupply(T object, Supplier<R> defaultValue, Function<T, R> runner) {
        return object == null ? defaultValue.get() : runner.apply(object);
    }

    public static <T> T log(T object) {
        return apply(object, ChocoFlavor.LOGGER::info);
    }

    public static <T, R> T log(T object, ObjectRunner<T, R> consumer) {
        justTry(() -> apply(consumer.create(object), GlobalUtils::log));
        return object;
    }

    public static void log(Object... objects) {
        ChocoFlavor.LOGGER.info(Arrays.stream(objects).map(o -> o == null ? "null" : o.toString()).collect(Collectors.joining(", ")));
    }


    public static void logAll(List<ItemStack> list) {
        ChocoFlavor.LOGGER.info(list.stream().map(o -> o == null ? "null" : o.toString()).collect(Collectors.joining(", ")));
    }

    public interface Scheduled {
        void run();
    }

    public static <T> boolean any(Predicate<T> predicate, T... others) {
        for (T other : others) {
            if(predicate.test(other)) {
                return true;
            }
        }
        return false;
    }

    public static int count(boolean... values) {
        int value = 0;
        for (boolean b : values) {
            if(b) value++;
        }
        return value;
    }

    public static boolean anyEquals(Object src, Object... others) {
        for (Object other : others) {
            if(src.equals(other)) {
                return true;
            }
        }
        return false;
    }

    public static boolean anyInstanceOf(Object src, Class<?>... clazzes) {
        for (Class<?> clazz: clazzes) {
            if(src.getClass().isAssignableFrom(clazz)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isSubClass(Class sub, Class sup) {
        return sup.isAssignableFrom(sub) || sub == sup;
    }
    public static boolean isSupClass(Class sup, Class sub) {
        return sup.isAssignableFrom(sub) || sub == sup;
    }

    public static Predicate<Class<?>> isSubClass(Class<?> clazz) {
        return parent -> isSubClass(clazz, parent);
    }

    public static <T, R> R runAsWithDefault(Object object, Class<T> clazz, R defaultValue, CSupplier<T, R> supplier) {
        if(object != null && isSubClass(object.getClass(), clazz)) {
            return supplier.getValue(clazz.cast(object));
        }
        return defaultValue;
    }

    public static <T, R> R runAsWithDefault(Object object, Class<T> clazz, R defaultValue, CSupplier<T, R> supplier, Consumer<R> consumer) {
        if(object != null && isSubClass(object.getClass(), clazz)) {
            return GlobalUtils.apply(supplier.getValue(clazz.cast(object)), consumer::accept);
        }
        return defaultValue;
    }

    public static <T, R> R runAs(Object object, Class<T> clazz, CSupplier<T, R> supplier, Consumer<R> consumer) {
        return runAsWithDefault(object, clazz, null, supplier, consumer);
    }

    public static <T, O> O applyAs(O o, Class<T> clazz, Consumer<T> consumer) {
        if(o != null && isSubClass(o.getClass(), clazz)) {
            consumer.accept(clazz.cast(o));
        }
        return o;
    }

    public static <T, R> R runAs(@Nullable Object object, Class<T> clazz, CSupplier<T, R> supplier) {
        return runAsWithDefault(object, clazz, null, supplier);
    }

    public static String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private static final Map<Scheduled, Timer> schedules = new HashMap<>();
    public static synchronized void executeAfter(Scheduled scheduled, long delay) {
        apply(schedules.put(scheduled, apply(new Timer(), it -> {
            it.schedule(new TimerTask() {
                public void run() {
                    scheduled.run();
                }
            }, delay);
        })), Timer::cancel);
////         Timer timer.cancel(); //this will cancel the current task. if there is no active task, nothing happens
//        Timer timer = new Timer();
//
//        TimerTask action = new TimerTask() {
//            public void run() {
//                YourClassType.abc(); //as you said in the comments: abc is a static method
//            }
//
//        };
//
//        timer.schedule(action, 60000); //this starts the task
    }

    public static interface TriPredicate<T, U, V> {
        boolean test(T t, U u, V v);
    }

    public static interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }

    public interface CSupplier<T, V> {
        V getValue(T t);
    }

    public interface CBiSupplier<T, U, V> {
        T getValue(U u, V v);
    }
}
