package me.koallann.p2ps.peer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

final class PeerViewerThread extends Thread {

    private final DatagramSocket streamingSocket;
    private final int packetSize;
    private final PeerStreaming.OnReceiveDataListener onReceiveDataListener;

    public PeerViewerThread(
        DatagramSocket streamingSocket,
        int packetSize,
        PeerStreaming.OnReceiveDataListener onReceiveDataListener
    ) {
        if (packetSize < 1) {
            throw new IllegalArgumentException("Packet size must be greater than 0");
        }
        this.streamingSocket = streamingSocket;
        this.packetSize = packetSize;
        this.onReceiveDataListener = onReceiveDataListener;
    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {
            try {
                final byte[] receiveBytes = new byte[packetSize];
                final DatagramPacket receivePacket = new DatagramPacket(receiveBytes, packetSize);

                streamingSocket.receive(receivePacket);
                onReceiveDataListener.onReceive(new ByteArrayInputStream(receiveBytes));
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
