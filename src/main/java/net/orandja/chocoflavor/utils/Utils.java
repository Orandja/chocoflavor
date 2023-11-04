package net.orandja.chocoflavor.utils;

import net.orandja.chocoflavor.ChocoFlavor;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class Utils {

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
        } catch(Exception ignore) {
        }
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

    public static <T> T log(T object) {
        return apply(object, ChocoFlavor.LOGGER::info);
    }

    public static <T, R> T log(T object, ObjectRunner<T, R> consumer) {
        justTry(() -> apply(consumer.create(object), Utils::log));
        return object;
    }

    public static void log(Object... objects) {
        ChocoFlavor.LOGGER.info(Arrays.stream(objects).map(o -> o.toString()).collect(Collectors.joining(", ")));
    }

    public interface Scheduled {
        void run();
    }

    public static boolean anyEquals(Object src, Object... others) {
        for (Object other : others) {
            if(src.equals(other)) {
                return true;
            }
        }
        return false;
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

}
