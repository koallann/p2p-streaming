package me.koallann.p2ps.util;

import java.util.Map;

public final class MapUtils {

    private MapUtils() {
        // This is a pure static class
    }

    public static <K, V, E extends Exception> V readOrElseThrow(
        Map<K, V> map,
        K key,
        E error
    ) throws E {
        final V value = map.get(key);
        if (value == null) {
            throw error;
        }
        return value;
    }

}
