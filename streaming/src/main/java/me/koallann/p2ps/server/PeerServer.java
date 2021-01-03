package me.koallann.p2ps.server;

import java.io.IOException;

public class PeerServer {

    private final PeerServerThread serverThread;

    public PeerServer(int port) throws IOException {
        this.serverThread = new PeerServerThread(port, null);
    }

    public void listen() {
        serverThread.start();
    }

    public void stop() {
        serverThread.interrupt();
    }

}
