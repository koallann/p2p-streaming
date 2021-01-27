package me.koallann.p2ps.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import me.koallann.p2ps.util.ByteUtils;

final class PeerServerThread extends Thread {

    private final ServerSocket serverSocket;
    private final int packetMaxSize;
    private final PeerServer.OnConnectionListener onConnectionListener;

    protected PeerServerThread(
        int port,
        int packetMaxSize,
        PeerServer.OnConnectionListener onConnectionListener
    ) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.packetMaxSize = packetMaxSize;
        this.onConnectionListener = onConnectionListener;
    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {
            try {
                final Socket conn = serverSocket.accept();

                final Request request = Request.from(
                    conn.getInetAddress(),
                    ByteUtils.read(conn.getInputStream(), packetMaxSize)
                );
                final Response response = onConnectionListener.onConnection(request);

                conn.getOutputStream().write(response.encode());
                conn.close();
            } catch (IOException e) {
                if (!isInterrupted()) {
                    e.printStackTrace();
                }
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
