package me.koallann.p2ps.command;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import me.koallann.p2ps.util.StringUtils;

public final class Response {

    public enum Type {
        OK, ERR
    }

    private Type type;
    private String errorCause;

    public Response(byte[] data) throws IOException, IllegalArgumentException {
        check(data);
    }

    public Type getType() {
        return type;
    }

    public String getErrorCause() {
        return errorCause;
    }

    private void check(byte[] data) throws IOException, IllegalArgumentException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
        final String firstLine = reader.readLine();

        try {
            final Type type = Type.valueOf(firstLine);
            this.type = type;

            if (type == Type.ERR) {
                reader.readLine();
                this.errorCause = reader.readLine();
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid data");
        } finally {
            reader.close();
        }
    }

    public static byte[] respondOK() {
        return Type.OK.name().getBytes();
    }

    public static byte[] respondError(String errorMessage) {
        return StringUtils.format("%s\n\nCause: %s", Type.OK.name(), errorMessage).getBytes();
    }

}
