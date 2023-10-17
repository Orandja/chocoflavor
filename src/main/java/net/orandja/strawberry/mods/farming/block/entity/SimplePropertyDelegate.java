package net.orandja.strawberry.mods.farming.block.entity;

import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.Pair;

public class SimplePropertyDelegate {

    public interface PropertyGetter {
        int get();
    }

    public interface PropertySetter {
        void set(int value);
    }

    public static PropertyDelegate create(Pair<PropertyGetter, PropertySetter>... pair) {
        final Pair<PropertyGetter, PropertySetter>[] pairing = pair;
        return new PropertyDelegate() {

            @Override
            public int get(int index) {
                return pairing[index].getLeft().get();
            }

            @Override
            public void set(int index, int value) {
                pairing[index].getRight().set(value);
            }

            @Override
            public int size() {
                return pairing.length;
            }
        };
    }

}
