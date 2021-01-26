package me.koallann.p2ps.command;

import me.koallann.p2ps.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public final class StreamingCommand extends Command {

    public static final String PARAM_CONTENT_LENGTH = "Content-Length";

    public final byte[] data;

    StreamingCommand(InetAddress from, byte[] data) {
        super(Type.STREAMING, from);
        this.data = data;
    }

    public static StreamingCommand from(InetAddress address, byte[] content) throws IllegalArgumentException {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("Content is null or empty");
        }

        return new StreamingCommand(address, content);
    }

    public static Request buildRequest(byte[] data) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(StringUtils.format("%s\n%s: %d\n\n", Type.STREAMING.name(), PARAM_CONTENT_LENGTH, data.length).getBytes());
        outputStream.write(data);
        outputStream.close();

        return new Request(InetAddress.getLocalHost(), outputStream.toByteArray());
    }

}
