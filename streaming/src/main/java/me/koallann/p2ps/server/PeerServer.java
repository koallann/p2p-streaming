package me.koallann.p2ps.server;

import java.io.BufferedReader;
import java.io.IOException;

public class PeerServer {

    private final PeerServerThread serverThread;

    public PeerServer(int port, OnConnectionListener onConnectionListener) throws IOException {
        this.serverThread = new PeerServerThread(port, onConnectionListener);
    }

    public void listen() {
        serverThread.start();
    }

    public void stop() {
        serverThread.interrupt();
    }

    @FunctionalInterface
    public interface OnConnectionListener {
        String onConnection(BufferedReader input);
    }

}
