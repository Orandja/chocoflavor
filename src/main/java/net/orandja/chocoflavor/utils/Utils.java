package net.orandja.chocoflavor.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public abstract class Utils {

    public interface ObjectCreator<T> {
        T create();
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

    public interface Scheduled {
        void run();
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
