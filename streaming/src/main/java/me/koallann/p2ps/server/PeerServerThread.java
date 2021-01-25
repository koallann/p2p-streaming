package me.koallann.p2ps.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import me.koallann.p2ps.util.ByteUtils;
import me.koallann.p2ps.command.Request;

final class PeerServerThread extends Thread {

    private static final int DATA_MAX_SIZE = 1024;

    private final ServerSocket serverSocket;
    private final PeerServer.OnConnectionListener onConnectionListener;

    protected PeerServerThread(
        int port,
        PeerServer.OnConnectionListener onConnectionListener
    ) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.onConnectionListener = onConnectionListener;
    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {
            try {
                final Socket conn = serverSocket.accept();

                final Request request = new Request(
                    conn.getInetAddress(),
                    ByteUtils.read(conn.getInputStream(), DATA_MAX_SIZE)
                );
                final byte[] response = onConnectionListener.onConnection(request);

                conn.getOutputStream().write(response);
                conn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
