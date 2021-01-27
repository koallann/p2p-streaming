package me.koallann.p2ps.server;

import java.io.IOException;

public final class PeerServer {

    private final PeerServerThread serverThread;

    public PeerServer(int port, int packetMaxSize, OnConnectionListener onConnectionListener) throws IOException {
        this.serverThread = new PeerServerThread(port, packetMaxSize, onConnectionListener);
    }

    public void listen() {
        serverThread.start();
    }

    public void stop() {
        serverThread.interrupt();
    }

    @FunctionalInterface
    public interface OnConnectionListener {
        Response onConnection(Request request) throws IOException;
    }

}
