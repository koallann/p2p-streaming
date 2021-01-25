package me.koallann.p2ps.util;

import java.io.IOException;
import java.io.InputStream;

public class ByteUtils {

    private ByteUtils() {
        // This is a pure static class
    }

    public static byte[] read(InputStream inputStream, int n) throws IOException {
        byte[] bytes = new byte[n];

        final int read = inputStream.read(bytes);
        if (read < n) {
            bytes = resizeArray(bytes, read);
        }

        return bytes;
    }

    public static byte[] resizeArray(byte[] src, int n) {
        final byte[] resized = new byte[n];
        System.arraycopy(src, 0, resized, 0, n);
        return resized;
    }

}
