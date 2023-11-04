package net.orandja.chocoflavor.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class MathUtils {

    public static class ConditionalValue<N extends Number>  {

        public enum Modifier {
            DIVIDE((it, mod) -> it.doubleValue() / mod.doubleValue()),
            MULTIPLY((it, mod) -> it.doubleValue() * it.doubleValue()),
            ADD((it, mod) -> it.doubleValue() + it.doubleValue()),
            SUBSTRACT((it, mod) -> it.doubleValue() - it.doubleValue());

            public interface Operation {
                Number apply(Number value, Number mod);
            }

            private final Operation operation;

            Modifier(Operation operation) {
                this.operation = operation;
            }

            public <N extends Number> N apply(N value, N modifier) {
                return (N) this.operation.apply(value, modifier);
            }
        }

        private N value;

        public ConditionalValue(N value) {
            this.value = value;
        }

        public static <T extends Number> ConditionalValue<T> of(T value) {
            return new ConditionalValue<T>(value);
        }

        public ConditionalValue<N> applyModifier(Predicate<N> supplier, Modifier modifier, Supplier<N> value) {
            if(supplier.test(this.value)) {
                this.value = modifier.apply(this.value, value.get());
            }

            return this;
        }

        public N getValue() {
            return this.value;
        }
    }

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
