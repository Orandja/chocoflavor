package net.orandja.chocoflavor.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

public class DefaultedMap<A, B> {

    private final Map<A, B[]> map = new HashMap<>();
    private final BiPredicate<A, A> predicate;

    public DefaultedMap(BiPredicate<A, A> predicate) {
        this.predicate = predicate;
    }

    public DefaultedMap add(A a, B[] bs) {
        this.map.put(a, bs);
        return this;
    }

    public B[] getFor(A a) {
        for (A aInMap : this.map.keySet()) {
            if(this.predicate.test(a, aInMap)) {
                return this.map.get(aInMap);
            }
        }

        return null;
    }

    public B getFirst(A a, B defaultB) {
        return GlobalUtils.runOrDefault(getFor(a), defaultB, it -> it[0]);
    }

    public B getFirst(A a) {
        return GlobalUtils.run(getFor(a), it -> it[0]);
    }

    public boolean contains(A a) {
        for (A aInMap : this.map.keySet()) {
            if(this.predicate.test(a, aInMap)) {
                return true;
            }
        }

        return false;
    }
}
