package me.koallann.p2ps.util;

import java.util.Locale;

public class StringUtils {

    private StringUtils() {
        // This is a pure static class
    }

    public static String format(String fmt, Object ...args) {
        return String.format(Locale.ENGLISH, fmt, args);
    }

}
