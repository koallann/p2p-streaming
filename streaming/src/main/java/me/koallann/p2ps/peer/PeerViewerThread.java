package me.koallann.p2ps.peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import me.koallann.p2ps.command.Request;

final class PeerViewerThread extends Thread {

    private final DatagramSocket streamingSocket;
    private final int dataMaxSize;
    private final PeerStreaming.OnReceiveDataListener onReceiveDataListener;

    public PeerViewerThread(
        DatagramSocket streamingSocket,
        int dataMaxSize,
        PeerStreaming.OnReceiveDataListener onReceiveDataListener
    ) {
        this.streamingSocket = streamingSocket;
        this.dataMaxSize = dataMaxSize;
        this.onReceiveDataListener = onReceiveDataListener;
    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {
            try {
                final byte[] receiveBytes = new byte[dataMaxSize];
                final DatagramPacket receivePacket = new DatagramPacket(receiveBytes, dataMaxSize);

                streamingSocket.receive(receivePacket);

                final Request request = new Request(
                    receivePacket.getAddress(),
                    receiveBytes
                );
                onReceiveDataListener.onReceive(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        streamingSocket.close();
    }

}
