package me.koallann.p2ps.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public final class PeerServerThread extends Thread {

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
                final BufferedReader connInput = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                final DataOutputStream connOutput = new DataOutputStream(conn.getOutputStream());
                final String response = onConnectionListener.onConnection(connInput);

                connOutput.writeBytes(response);
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
