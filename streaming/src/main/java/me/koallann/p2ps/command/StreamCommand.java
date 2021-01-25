package me.koallann.p2ps.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;

import me.koallann.p2ps.util.StringUtils;

public final class StreamCommand extends Command {

    public final byte[] data;

    StreamCommand(byte[] data) {
        super(Type.STREAM);
        this.data = data;
    }

    public static StreamCommand from(byte[] requestBody) throws IllegalArgumentException {
        if (requestBody == null || requestBody.length == 0) {
            throw new IllegalArgumentException("Body is null or empty");
        }

        return new StreamCommand(requestBody);
    }

    public static Request buildRequest(byte[] data) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(StringUtils.format("%s\n\n", Type.STREAM.name()).getBytes());
        outputStream.write(data);
        outputStream.close();

        return new Request(InetAddress.getLocalHost(), outputStream.toByteArray());
    }

}
