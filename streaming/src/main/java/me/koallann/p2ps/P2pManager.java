package me.koallann.p2ps;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.koallann.p2ps.command.ConnectMeCommand;
import me.koallann.p2ps.server.Request;
import me.koallann.p2ps.server.Response;
import me.koallann.p2ps.command.StreamingCommand;
import me.koallann.p2ps.peer.Peer;
import me.koallann.p2ps.peer.PeerStreaming;
import me.koallann.p2ps.server.PeerServer;
import me.koallann.p2ps.util.ByteUtils;

public final class P2pManager {

    private static final int SERVER_PACKET_MAX_SIZE = 64;
    private static final int STREAMING_PACKET_MAX_SIZE = 1024;

    private final int serverPort;
    private final PeerServer server;
    private final Map<String, PeerStreaming> streams;
    private final Map<String, Peer> connectMeRequests;

    private final OnReceiveStreamingListener onReceiveStreamingListener;

    public P2pManager(
        int serverPort,
        OnReceiveStreamingListener onReceiveStreamingListener
    ) throws IOException {
        this.serverPort = serverPort;
        this.server = new PeerServer(serverPort, SERVER_PACKET_MAX_SIZE, this::handleServerIncoming);
        this.streams = new HashMap<>();
        this.connectMeRequests = new HashMap<>();
        this.onReceiveStreamingListener = onReceiveStreamingListener;
    }

    public void start() {
        server.listen();
    }

    public void stop() {
        server.stop();
        streams.values().forEach(PeerStreaming::stop);
        streams.clear();
        connectMeRequests.clear();
    }

    public List<PeerStreaming> getStreams() {
        return new ArrayList<>(streams.values());
    }

    public List<Peer> getConnectMeRequests() {
        return new ArrayList<>(connectMeRequests.values());
    }

    public void requestPeerToConnectMe(String host) throws IOException {
        if (streams.containsKey(host)) {
            return;
        }

        final PeerStreaming streaming = new PeerStreaming(STREAMING_PACKET_MAX_SIZE, this::handleStreamingIncoming);
        streams.put(host, streaming);

        if (connectMeRequests.containsKey(host)) {
            streaming.setPeer(connectMeRequests.get(host));
        }

        streaming.start();

        new Thread(() -> {
            try {
                makeConnectMeRequest(host, streaming.getViewerPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void makeStreaming(byte[] content) {
        try {
            final StreamingCommand cmd = new StreamingCommand(InetAddress.getLocalHost(), content);
            final byte[] requestEncoded = cmd.buildRequest().encode();

            for (PeerStreaming streaming : streams.values()) {
                streaming.send(requestEncoded);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeConnectMeRequest(String host, int viewerPort) throws IOException {
        final Socket socket = new Socket(InetAddress.getByName(host), serverPort);

        final ConnectMeCommand cmd = new ConnectMeCommand(InetAddress.getLocalHost(), viewerPort);
        socket.getOutputStream().write(cmd.buildRequest().encode());

        byte[] responseBytes = ByteUtils.read(socket.getInputStream(), SERVER_PACKET_MAX_SIZE);
        final Response response = new Response(responseBytes);

        socket.close();
    }

    private synchronized byte[] handleServerIncoming(Request request) {
        try {
            final ConnectMeCommand cmd = ConnectMeCommand.from(request);
            return onConnectMeCommand(cmd);
        } catch (IllegalArgumentException e) {
            return Response.respondError("Invalid command");
        }
    }

    private synchronized void handleStreamingIncoming(Request request) {
        try {
            final StreamingCommand cmd = StreamingCommand.from(request);
            onStreamingCommand((StreamingCommand) cmd);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private byte[] onConnectMeCommand(ConnectMeCommand cmd) {
        final Peer peer = new Peer(cmd.src.getHostAddress(), cmd.port);
        connectMeRequests.put(peer.host, peer);

        if (streams.containsKey(peer.host)) {
            streams.get(peer.host).setPeer(peer);
        }

        return Response.respondOK();
    }

    private void onStreamingCommand(StreamingCommand cmd) {
        onReceiveStreamingListener.onReceiveStreaming(cmd);
    }

    @FunctionalInterface
    public interface OnReceiveStreamingListener {
        void onReceiveStreaming(StreamingCommand cmd);
    }

}
