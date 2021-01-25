package me.koallann.p2ps.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;

import me.koallann.p2ps.util.StringUtils;

public final class StreamingCommand extends Command {

    public static final String PARAM_CONTENT_LENGTH = "Content-Length";

    public final byte[] data;

    StreamingCommand(byte[] data) {
        super(Type.STREAMING);
        this.data = data;
    }

    public static StreamingCommand from(byte[] requestBody) throws IllegalArgumentException {
        if (requestBody == null || requestBody.length == 0) {
            throw new IllegalArgumentException("Body is null or empty");
        }

        return new StreamingCommand(requestBody);
    }

    public static Request buildRequest(byte[] data) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(StringUtils.format("%s\n%s: %d\n\n", Type.STREAMING.name(), PARAM_CONTENT_LENGTH, data.length).getBytes());
        outputStream.write(data);
        outputStream.close();

        return new Request(InetAddress.getLocalHost(), outputStream.toByteArray());
    }

}
