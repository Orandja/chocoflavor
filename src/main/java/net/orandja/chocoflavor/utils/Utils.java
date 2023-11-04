package net.orandja.chocoflavor.utils;

import joptsimple.internal.Strings;
import net.orandja.chocoflavor.ChocoFlavor;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class Utils {

    public interface ObjectCreator<T> {
        T create();
    }

    public interface ObjectRunner<T, R> {
        R create(T value);
    }

    public static <T> T create(ObjectCreator<T> creator, Consumer<T> consumer) {
        T object = creator.create();
        consumer.accept(object);
        return object;
    }

    public static <T> T apply(T object, Consumer<T> consumer) {
        if(object != null)
            consumer.accept(object);

        return object;
    }

    public static <T, R> R run(T object, ObjectRunner<T, R> runner) {
        return runner.create(object);
    }

    public static <T> T log(T object) {
        return apply(object, ChocoFlavor.LOGGER::info);
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
