package me.koallann.p2ps.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public final class Response {

    public enum Type {
        OK, ERR
    }

    public final Type type;
    public final String errorCause;

    public Response(Type type, String errorCause) {
        this.type = type;
        this.errorCause = errorCause;
    }

    public byte[] encode() throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(type.name().getBytes());

        if (type == Type.ERR && errorCause != null) {
            outputStream.write(errorCause.getBytes());
        }

        outputStream.close();
        return outputStream.toByteArray();
    }

    public static Response from(byte[] bytes) throws IOException, IllegalArgumentException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
        final String typeLine = reader.readLine();
        final Type type = Type.valueOf(typeLine);

        String errorCause = null;
        if (type == Type.ERR) {
            errorCause = reader.readLine();
        }

        reader.close();
        return new Response(type, errorCause);
    }

}
