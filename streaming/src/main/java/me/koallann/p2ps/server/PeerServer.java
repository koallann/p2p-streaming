package me.koallann.p2ps.server;

import java.io.IOException;

import me.koallann.p2ps.command.Request;

public final class PeerServer {

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
        byte[] onConnection(Request request) throws IOException;
    }

}
