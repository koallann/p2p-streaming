package me.koallann.p2ps.peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.koallann.p2ps.command.Request;

public final class PeerStreaming {

    private Peer peer;
    private final DatagramSocket streamingSocket;
    private final ExecutorService emitter;
    private final PeerViewerThread viewerThread;

    public PeerStreaming(int dataMaxSize, OnReceiveDataListener onReceiveDataListener) throws SocketException {
        this.streamingSocket = new DatagramSocket();
        this.emitter = Executors.newSingleThreadExecutor();
        this.viewerThread = new PeerViewerThread(streamingSocket, dataMaxSize, onReceiveDataListener);
    }

    public int getViewerPort() {
        return streamingSocket.getPort();
    }

    public Peer getPeer() {
        return this.peer;
    }

    public void setPeer(Peer peer) {
        this.peer = peer;
    }

    public void start() {
        viewerThread.start();
    }

    public void stop() {
        emitter.shutdown();
        viewerThread.interrupt();
        streamingSocket.close();
    }

    public void send(byte[] data) {
        if (peer == null) {
            return;
        }
        emitter.submit(() -> {
            try {
                final DatagramPacket packet = new DatagramPacket(
                    data,
                    data.length,
                    InetAddress.getByName(peer.host),
                    peer.viewerPort
                );
                streamingSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FunctionalInterface
    public interface OnReceiveDataListener {
        void onReceive(Request request);
    }

}
