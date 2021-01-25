package me.koallann.p2ps.command;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class Request {

    public final InetAddress from;
    public final byte[] data;

    public Request(InetAddress from, byte[] data) throws IOException, IllegalArgumentException {
        this.from = from;
        this.data = data;
        check();
    }

    private void check() throws IOException, IllegalArgumentException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
        final String firstLine = reader.readLine();

        try {
            Command.Type.valueOf(firstLine);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid data");
        } finally {
            reader.close();
        }
    }

}
