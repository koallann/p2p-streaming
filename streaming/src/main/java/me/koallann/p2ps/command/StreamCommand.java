package me.koallann.p2ps.command;

public class StreamCommand extends Command {

    final byte[] data;

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

}
