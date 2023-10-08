package net.orandja.chocoflavor.utils;

import java.util.logging.Logger;

public class VWUtils {
    public static <T> T f(T object) {
        Logger.getAnonymousLogger().info(object.toString());
        return object;
    }

    public static void f(Object... objects) {
        for (Object object : objects) {
            f(object);
        }
    }

}
