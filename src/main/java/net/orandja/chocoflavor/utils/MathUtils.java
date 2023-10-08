package net.orandja.chocoflavor.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.util.function.Predicate;

public abstract class MathUtils {

    public interface GridConsumer {
        void accept(int x, int y);
    }

    public static void grid(int width, GridConsumer consumer) {
        grid(width, 1, consumer);
    }

    public static void grid(int width, int height, GridConsumer consumer) {
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                consumer.accept(x, y);
            }
        }
    }

    public static Predicate<Entity> inRange(BlockPos pos, double distance) {
        return entity -> entity.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) < distance;
    }

    public static int indexOf(Object[] objects, Object object) {
        for(int i = 0; i < objects.length; i++) {
            if(objects[i].equals(object)) {
                return i;
            }
        }
        return -1;
    }

    public static int nextIndexOf(Object[] objects, Object object) {
        return (indexOf(objects, object) + 1) % objects.length;
    }
}
