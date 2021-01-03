package me.koallann.p2ps.peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerStreaming {

    private final Peer peer;
    private final DatagramSocket streamingSocket;
    private final ExecutorService emitter;
    private final PeerViewerThread viewerThread;

    public PeerStreaming(Peer peer) throws SocketException {
        this.peer = peer;
        this.streamingSocket = new DatagramSocket();
        this.emitter = Executors.newSingleThreadExecutor();
        this.viewerThread = new PeerViewerThread(streamingSocket, 1024, null);
    }

    public int getViewerPort() {
        return streamingSocket.getPort();
    }

    public void startViewing() {
        viewerThread.start();
    }

    public void stopViewing() {
        viewerThread.interrupt();
    }

    public void stopStreaming() {
        emitter.shutdown();
        viewerThread.interrupt();
        streamingSocket.close();
    }

    public void send(byte[] data) {
        emitter.submit(() -> {
            try {
                final DatagramPacket packet = new DatagramPacket(
                    data,
                    data.length,
                    peer.getAddress(),
                    peer.getViewerPort()
                );
                streamingSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
