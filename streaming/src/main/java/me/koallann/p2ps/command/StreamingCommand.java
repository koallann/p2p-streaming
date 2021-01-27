package me.koallann.p2ps.command;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import me.koallann.p2ps.util.ByteUtils;
import me.koallann.p2ps.util.StringUtils;

public final class StreamingCommand extends Command {

    private static final String PARAM_CONTENT_LENGTH = "Content-Length";

    public final byte[] content;

    public StreamingCommand(InetAddress src, byte[] content) {
        super(Type.STREAMING, src);
        this.content = content;
    }

    public Request buildRequest() {
        final Map<String, String> params = new HashMap<>();
        params.put(PARAM_CONTENT_LENGTH, Integer.toString(content.length));

        return new Request(src, type, params, content);
    }

    public static StreamingCommand from(Request request) throws IllegalArgumentException {
        final byte[] content = request.content;
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("Content is null or empty");
        }

        final String contentLengthParam = request.params.get(PARAM_CONTENT_LENGTH);
        if (contentLengthParam == null) {
            throw new IllegalArgumentException(StringUtils.format("Param \"%s\" not set", PARAM_CONTENT_LENGTH));
        }

        final int contentLength = Integer.parseInt(contentLengthParam);
        final byte[] contentResized = ByteUtils.resizeArray(content, contentLength);

        return new StreamingCommand(request.src, contentResized);
    }

}
